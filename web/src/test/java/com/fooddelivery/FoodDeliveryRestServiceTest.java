package com.fooddelivery;

import com.fooddelivery.model.FoodModel;
import com.fooddelivery.model.OrderModel;
import com.fooddelivery.domain.Cart;
import com.fooddelivery.domain.Food;
import com.fooddelivery.domain.Order;
import com.fooddelivery.domain.OrderItem;
import com.fooddelivery.repository.CustomerRepository;
import com.fooddelivery.repository.OrderRepository;
import com.fooddelivery.service.FoodDeliveryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FoodDeliveryRestServiceTest {
    private static final String USERNAME = "Smith";
    private static final String PASSWORD = "SmithSecret";
    private static final String OTHER_USERNAME = "Jane";
    private static final String INVALID_USER = "invalid-user";
    private static final String WRONG_PASSWORD = "wrong-password";

    private static final String ORDERS_PATH = "/api/orders";
    private static final String FOODS_PATH = "/api/foods";

    private static final long ORDER_ID = 1L;
    private static final int NO_OF_FOODS = 5;
    private static final long NOT_EXISTING_ORDER_ID = 1000L;

    private static final String VALID_CART_FILE_NAME = "ValidCartExample.json";
    private static final String EMPTY_CART_FILE_NAME = "EmptyCartExample.json";

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private FoodDeliveryService foodDeliveryService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @DisplayName("Login should authenticate when user/pwd is valid: " + USERNAME + "/" + PASSWORD)
    public void testLoginShouldAuthenticateWhenValidUserAndPassword() throws Exception {
        MvcResult result = mockMvc
                .perform(formLogin()
                        .user(USERNAME)
                        .password(PASSWORD))
                .andExpect(authenticated()
                        .withUsername(USERNAME))
                .andReturn();
    }

    @Test
    @DisplayName("Login should not authenticate when password is wrong: " + USERNAME + "/" + WRONG_PASSWORD)
    public void testLoginShouldFailWhenValidUserAndIncorrectPassword() throws Exception {
        MvcResult result = mockMvc
                .perform(formLogin()
                        .user(USERNAME)
                        .password(WRONG_PASSWORD))
                .andExpect(unauthenticated())
                .andReturn();
    }

    @Test
    @DisplayName("Login should not authenticate when user is invalid: " + INVALID_USER + "/" + PASSWORD)
    public void testLoginShouldFailWhenInvalidUser() throws Exception {
        MvcResult result = mockMvc
                .perform(formLogin()
                        .user(INVALID_USER)
                        .password(PASSWORD))
                .andExpect(unauthenticated())
                .andReturn();
    }

    @Test
    @DisplayName("get " + FOODS_PATH + " should respond with 401 when not authenticated")
    public void testGetFoodsShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        mockMvc
                .perform(get(FOODS_PATH))
                .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    @DisplayName("get " + ORDERS_PATH + " should respond with 401 when not authenticated")
    public void testGetOrdersShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        mockMvc
                .perform(get(ORDERS_PATH))
                .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    @DisplayName("post " + ORDERS_PATH + " should respond with 401 when not authenticated")
    public void testPostOrdersShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        mockMvc
                .perform(post(ORDERS_PATH).with(csrf()))
                .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    @DisplayName("get " + ORDERS_PATH + "/" + ORDER_ID + " should respond with 401 when not authenticated")
    public void testGetOrderByOrderIdShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        mockMvc
                .perform(get(ORDERS_PATH + "/" + ORDER_ID))
                .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    @WithMockUser(username = USERNAME)
    @DisplayName("get " + FOODS_PATH + " should respond " + NO_OF_FOODS)
    public void testGetFoodsShouldReturnFoodsCollectionAndSizeCorrect() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(get(FOODS_PATH))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        FoodModel[] actual = objectMapper.readValue(responseBody, FoodModel[].class);
        Assertions.assertEquals(NO_OF_FOODS, actual.length, "The number of returned foods is incorrect.");
    }

    @Test
    @WithMockUser(username = OTHER_USERNAME)
    @DisplayName("get " + ORDERS_PATH
            + " should respond with empty array when authenticated customer doesn't have any order")
    public void testGetOrdersShouldReturnEmptyArrayWhenAuthenticatedCustomerDoesNotHaveOrder() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(get(ORDERS_PATH))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        OrderModel[] actual = objectMapper.readValue(responseBody, OrderModel[].class);
        Assertions.assertEquals(0, actual.length, OTHER_USERNAME + " should not have any orders.");
    }

    @Test
    @WithMockUser(username = USERNAME)
    @DisplayName("get " + ORDERS_PATH
            + " should respond array of orders when authenticated customer has")
    public void testGetOrdersShouldReturnOrdersWhenAuthenticatedCustomerHasOrders() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(get(ORDERS_PATH))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        OrderModel[] actual = objectMapper.readValue(responseBody, OrderModel[].class);
        Assertions.assertEquals(2, actual.length, " number of orders owned by " + USERNAME);
    }

    @Test
    @WithMockUser(username = USERNAME)
    @DisplayName("get " + ORDERS_PATH + "/" + ORDER_ID
            + " should respond the orders when authenticated customer owns the order")
    public void testGetOrderByIdShouldReturnTheOrderWhenAuthenticatedCustomerOwnsIt() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(get(ORDERS_PATH + "/" + ORDER_ID))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        OrderModel order = objectMapper.readValue(responseBody, OrderModel.class);
        Assertions.assertEquals(new BigDecimal("20.00"), order.getPrice(), "order price");
    }

    @Test
    @WithMockUser(username = OTHER_USERNAME)
    @DisplayName("get " + ORDERS_PATH + "/" + ORDER_ID
            + " should return FORBIDDEN if accessed by other than owner customer")
    public void testGetOrderByOrderIdShouldReturnForbiddenWhenOrderIsNotOwnedByAuthenticatedCustomer() throws Exception {
        mockMvc
                .perform(get(ORDERS_PATH + "/" + ORDER_ID))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @WithMockUser(username = USERNAME)
    @DisplayName("get " + ORDERS_PATH + "/" + NOT_EXISTING_ORDER_ID
            + " should return FORBIDDEN if accessed by other than owner customer")
    public void testGetOrderByOrderIdShouldReturnNotFoundWhenOrderDoesNotExists() throws Exception {
        mockMvc
                .perform(get(ORDERS_PATH + "/" + NOT_EXISTING_ORDER_ID))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    @WithMockUser(username = USERNAME)
    @DisplayName("post " + ORDERS_PATH + " should respond with 400 when cart is empty")
    void testPostOrderServiceSlashOrdersShouldReturnWithBadRequestWhenCartIsEmpty() throws Exception {
        mockMvc
                .perform(post(ORDERS_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fromFile(EMPTY_CART_FILE_NAME)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @WithMockUser(username = USERNAME)
    @DisplayName("post " + ORDERS_PATH
            + " should respond 201 and the created order")
    void testCreateOrderShouldReturnOrderWhenOrderCreated() throws Exception {
        BigDecimal balanceBefore = balance(USERNAME);
        long ordersBefore = orderRepository.count();
        MvcResult mvcResult = mockMvc
                .perform(post(ORDERS_PATH)
                    .with(csrf().asHeader())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(fromFile(VALID_CART_FILE_NAME)))
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        OrderModel actual = objectMapper.readValue(responseBody, OrderModel.class);

        Assertions.assertEquals(new BigDecimal("45.00"), actual.getPrice(), "order price");
        Assertions.assertEquals(1, actual.getOrderItemModels().size(), "number of order items");
        Assertions.assertEquals(1, actual.getOrderItemModels().get(0).getFoodModel().getId(),
                "food id");
        assertEquals(new BigDecimal("45.00"), actual.getOrderItemModels().get(0).getPrice());
        assertEquals(balanceBefore.subtract(actual.getPrice()), balance(USERNAME));
        assertEquals(ordersBefore + 1, orderRepository.count());
        assertEquals(actual.getPrice(), orderRepository.findById(actual.getId()).orElseThrow().getPrice());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "0.01", "-100", "999999"})
    @WithMockUser(username = USERNAME)
    void shouldIgnoreManipulatedPricesAndChargeForAllItems(String submittedPrice) throws Exception {
        String cart = """
                {"price": %s, "orderItemModels": [
                  {"foodId": 1, "pieces": 3, "price": %s, "foodModel": {"id": 1, "price": %s}},
                  {"foodId": 2, "pieces": 2, "price": %s}
                ]}
                """.formatted(submittedPrice, submittedPrice, submittedPrice, submittedPrice);
        BigDecimal balanceBefore = balance(USERNAME);
        long ordersBefore = orderRepository.count();

        MvcResult result = mockMvc.perform(post(ORDERS_PATH).with(csrf().asHeader())
                        .contentType(MediaType.APPLICATION_JSON).content(cart))
                .andExpect(status().isCreated()).andReturn();
        OrderModel created = objectMapper.readValue(result.getResponse().getContentAsString(), OrderModel.class);

        assertEquals(new BigDecimal("71.00"), created.getPrice());
        assertEquals(new BigDecimal("45.00"), created.getOrderItemModels().get(0).getPrice());
        assertEquals(new BigDecimal("26.00"), created.getOrderItemModels().get(1).getPrice());
        assertEquals(new BigDecimal("15.00"), created.getOrderItemModels().get(0).getFoodModel().getPrice());
        assertEquals(balanceBefore.subtract(new BigDecimal("71.00")), balance(USERNAME));
        assertEquals(ordersBefore + 1, orderRepository.count());
        Order saved = orderRepository.findById(created.getId()).orElseThrow();
        assertEquals(created.getPrice(), saved.getPrice());
        assertEquals(saved.getPrice(), saved.getOrderItems().stream()
                .map(OrderItem::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    @Test
    @WithMockUser(username = "John")
    void shouldRejectUnderpricedOrderWhenRealTotalExceedsBalance() throws Exception {
        BigDecimal balanceBefore = balance("John");
        long ordersBefore = orderRepository.count();
        mockMvc.perform(post(ORDERS_PATH).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"price": 0.01, "orderItemModels": [{"foodId": 1, "pieces": 3, "price": 0.01}]}
                                """))
                .andExpect(status().isBadRequest());
        assertEquals(balanceBefore, balance("John"));
        assertEquals(ordersBefore, orderRepository.count());
    }

    @Test
    void shouldRecalculatePricesAtTheServiceBoundary() {
        Food submittedFood = new Food();
        submittedFood.setId(1L);
        submittedFood.setPrice(new BigDecimal("0.01"));
        OrderItem submittedItem = new OrderItem();
        submittedItem.setFood(submittedFood);
        submittedItem.setPieces(3);
        submittedItem.setPrice(new BigDecimal("0.01"));
        Cart cart = new Cart();
        cart.getOrderItems().add(submittedItem);
        cart.setPrice(new BigDecimal("0.01"));

        Order order = foodDeliveryService.createOrder(USERNAME, cart);

        assertEquals(new BigDecimal("45.00"), order.getPrice());
        assertEquals(new BigDecimal("45.00"), order.getOrderItems().get(0).getPrice());
        assertEquals(new BigDecimal("15.00"), order.getOrderItems().get(0).getFood().getPrice());
        assertEquals(new BigDecimal("55.00"), balance(USERNAME));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}", "{\"orderItemModels\": null}", "{\"orderItemModels\": [null]}",
            "{\"orderItemModels\": [{\"foodId\": 1}]}",
            "{\"orderItemModels\": [{\"foodId\": 1, \"pieces\": null}]}",
            "{\"orderItemModels\": [{\"foodId\": 1, \"pieces\": 0}]}",
            "{\"orderItemModels\": [{\"foodId\": 1, \"pieces\": -3}]}",
            "{\"orderItemModels\": [{\"pieces\": 3}]}",
            "{\"orderItemModels\": [{\"foodId\": 0, \"pieces\": 3}]}"
    })
    @WithMockUser(username = USERNAME)
    void shouldRejectInvalidCartWithoutChangingState(String cart) throws Exception {
        BigDecimal balanceBefore = balance(USERNAME);
        long ordersBefore = orderRepository.count();
        mockMvc.perform(post(ORDERS_PATH).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(cart))
                .andExpect(status().isBadRequest());
        assertEquals(balanceBefore, balance(USERNAME));
        assertEquals(ordersBefore, orderRepository.count());
    }

    @Test
    @WithMockUser(username = USERNAME)
    void shouldRejectUnknownFoodWithoutChangingState() throws Exception {
        BigDecimal balanceBefore = balance(USERNAME);
        long ordersBefore = orderRepository.count();
        mockMvc.perform(post(ORDERS_PATH).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderItemModels\": [{\"foodId\": 9999, \"pieces\": 1}]}"))
                .andExpect(status().isNotFound());
        assertEquals(balanceBefore, balance(USERNAME));
        assertEquals(ordersBefore, orderRepository.count());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    @WithMockUser(username = USERNAME)
    void shouldRejectMissingOrInvalidCsrfWithoutChangingState(boolean invalidToken) throws Exception {
        BigDecimal balanceBefore = balance(USERNAME);
        long ordersBefore = orderRepository.count();
        var request = post(ORDERS_PATH).contentType(MediaType.APPLICATION_JSON)
                .content(fromFile(VALID_CART_FILE_NAME));
        if (invalidToken) {
            request.with(csrf().asHeader().useInvalidToken());
        }
        mockMvc.perform(request).andExpect(status().isForbidden());
        assertEquals(balanceBefore, balance(USERNAME));
        assertEquals(ordersBefore, orderRepository.count());
    }

    @Test
    void shouldRejectLoginWithoutCsrfToken() throws Exception {
        mockMvc.perform(post("/login").param("username", USERNAME).param("password", PASSWORD))
                .andExpect(status().isForbidden()).andExpect(unauthenticated());
    }

    @Test
    void shouldRequireAuthenticationToFetchApiCsrfToken() throws Exception {
        mockMvc.perform(get("/api/csrf")).andExpect(status().isUnauthorized());
    }

    @Test
    void shouldUseSessionCsrfTokenForCheckoutAndLogout() throws Exception {
        MvcResult loginPage = mockMvc.perform(get("/login")).andExpect(status().isOk()).andReturn();
        CsrfToken loginToken = (CsrfToken) loginPage.getRequest().getAttribute(CsrfToken.class.getName());
        MockHttpSession session = (MockHttpSession) loginPage.getRequest().getSession(false);
        MvcResult login = mockMvc.perform(post("/login").session(session)
                        .param("username", USERNAME).param("password", PASSWORD)
                        .param(loginToken.getParameterName(), loginToken.getToken()))
                .andExpect(authenticated().withUsername(USERNAME)).andReturn();
        session = (MockHttpSession) login.getRequest().getSession(false);

        MvcResult tokenResponse = mockMvc.perform(get("/api/csrf").session(session))
                .andExpect(status().isOk()).andReturn();
        var token = objectMapper.readTree(tokenResponse.getResponse().getContentAsString());
        String headerName = token.get("headerName").asText();
        String tokenValue = token.get("token").asText();
        long ordersBefore = orderRepository.count();

        mockMvc.perform(post(ORDERS_PATH).session(session)
                        .header(headerName, loginToken.getToken())
                        .contentType(MediaType.APPLICATION_JSON).content(fromFile(VALID_CART_FILE_NAME)))
                .andExpect(status().isForbidden());
        assertEquals(new BigDecimal("100.00"), balance(USERNAME));
        assertEquals(ordersBefore, orderRepository.count());

        mockMvc.perform(post(ORDERS_PATH).session(session).header(headerName, tokenValue)
                        .contentType(MediaType.APPLICATION_JSON).content(fromFile(VALID_CART_FILE_NAME)))
                .andExpect(status().isCreated());
        assertEquals(new BigDecimal("55.00"), balance(USERNAME));

        mockMvc.perform(post("/logout").session(session)).andExpect(status().isForbidden());
        mockMvc.perform(get(FOODS_PATH).session(session)).andExpect(status().isOk());
        mockMvc.perform(post("/logout").session(session).header(headerName, tokenValue))
                .andExpect(status().is3xxRedirection()).andExpect(unauthenticated());
    }

    private BigDecimal balance(String username) {
        return customerRepository.findByUserName(username).orElseThrow().getBalance();
    }

    private byte[] fromFile(String path) throws IOException {
        return new ClassPathResource(path).getInputStream().readAllBytes();
    }
}
