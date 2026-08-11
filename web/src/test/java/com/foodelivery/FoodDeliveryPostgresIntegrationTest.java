package com.foodelivery;

import com.foodelivery.domain.Customer;
import com.foodelivery.model.OrderModel;
import com.foodelivery.repository.CustomerRepository;
import com.foodelivery.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class FoodDeliveryPostgresIntegrationTest {

    private static final String ORDERS_PATH = "/api/orders";
    private static final String FOODS_PATH = "/api/foods";
    private static final String SMITH = "Smith";
    private static final String JANE = "Jane";
    private static final String JOHN = "John";
    private static final long SMITH_ORDER_ID = 1L;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("food_delivery")
            .withUsername("food_delivery")
            .withPassword("food_delivery");

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @DisplayName("GET /api/foods returns 401 when not authenticated")
    void shouldRejectUnauthenticatedFoodRequest() throws Exception {
        mockMvc.perform(get(FOODS_PATH))
                .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    @WithMockUser(username = JANE)
    @DisplayName("GET /api/orders/{id} returns 403 when order belongs to another customer")
    void shouldForbidAccessToAnotherCustomersOrder() throws Exception {
        mockMvc.perform(get(ORDERS_PATH + "/" + SMITH_ORDER_ID))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @WithMockUser(username = SMITH)
    @DisplayName("POST /api/orders creates order and reduces customer balance")
    void shouldCreateOrderAndReduceBalance() throws Exception {
        BigDecimal balanceBefore = customerRepository.findByUserName(SMITH).orElseThrow().getBalance();
        long ordersBefore = orderRepository.count();

        MvcResult result = mockMvc.perform(post(ORDERS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fromFile("ValidCartExample.json")))
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andReturn();

        OrderModel created = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                OrderModel.class
        );

        Customer smithAfter = customerRepository.findByUserName(SMITH).orElseThrow();

        assertEquals(new BigDecimal("45"), created.getPrice());
        assertEquals(balanceBefore.subtract(new BigDecimal("45")).setScale(2), smithAfter.getBalance());
        assertEquals(ordersBefore + 1, orderRepository.count());
    }

    @Test
    @WithMockUser(username = JOHN)
    @DisplayName("POST /api/orders returns 400 on insufficient balance without changing state")
    void shouldRejectOrderWhenBalanceIsInsufficient() throws Exception {
        BigDecimal balanceBefore = customerRepository.findByUserName(JOHN).orElseThrow().getBalance();
        long ordersBefore = orderRepository.count();

        mockMvc.perform(post(ORDERS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fromFile("ValidCartExample.json")))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        Customer johnAfter = customerRepository.findByUserName(JOHN).orElseThrow();

        assertEquals(balanceBefore, johnAfter.getBalance());
        assertEquals(ordersBefore, orderRepository.count());
    }

    private byte[] fromFile(String path) throws IOException {
        return new ClassPathResource(path).getInputStream().readAllBytes();
    }
}
