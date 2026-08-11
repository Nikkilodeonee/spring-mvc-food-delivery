package com.fooddelivery.service;

import com.fooddelivery.domain.*;
import com.fooddelivery.repository.CustomerRepository;
import com.fooddelivery.repository.FoodRepository;
import com.fooddelivery.repository.OrderRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class DefaultFoodDeliveryService implements FoodDeliveryService {

    private final CustomerRepository customerRepository;
    private final FoodRepository foodRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;

    public DefaultFoodDeliveryService(
            CustomerRepository customerRepository,
            FoodRepository foodRepository,
            OrderRepository orderRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.customerRepository = customerRepository;
        this.foodRepository = foodRepository;
        this.orderRepository = orderRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public Customer authenticate(Credentials credentials) {
        Customer customer = customerRepository.findByUserName(credentials.getUserName())
                .orElseThrow(() -> new AuthenticationException("Invalid username or password."));

        if (!passwordEncoder.matches(credentials.getPassword(), customer.getPassword())) {
            throw new AuthenticationException("Invalid username or password.");
        }

        return customer;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Food> getFoods() {
        return foodRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrders(String userName) {
        return orderRepository.findByCustomer_UserName(userName);
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderById(long orderId, String userName) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found."));

        if (!order.getCustomer().getUserName().equals(userName)) {
            throw new AccessDeniedException("Order is not owned by the authenticated customer.");
        }

        return order;
    }

    @Override
    @Transactional
    public Order createOrder(String userName, Cart cart) {
        Customer customer = customerRepository.findByUserName(userName)
                .orElseThrow(() -> new NoSuchElementException("Customer not found."));

        if (cart.getOrderItems() == null || cart.getOrderItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty.");
        }

        customer.setCart(cart);
        return createOrder(customer);
    }

    @Override
    public void updateCart(Customer customer, Food food, int pieces) {
        if (pieces < 0) {
            throw new IllegalArgumentException("Pieces cannot be negative.");
        }

        Cart cart = customer.getCart();

        OrderItem existingItem = cart.getOrderItems()
                .stream()
                .filter(item -> item.getFood().equals(food))
                .findFirst()
                .orElse(null);

        if (existingItem == null && pieces == 0) {
            throw new IllegalArgumentException("Cannot remove food that is not in the cart.");
        }

        if (existingItem == null) {
            OrderItem newItem = new OrderItem();
            newItem.setFood(food);
            newItem.setPieces(pieces);
            newItem.setPrice(food.getPrice().multiply(BigDecimal.valueOf(pieces)).setScale(2));

            cart.getOrderItems().add(newItem);
        } else if (pieces == 0) {
            cart.getOrderItems().remove(existingItem);
        } else {
            existingItem.setPieces(pieces);
            existingItem.setPrice(food.getPrice().multiply(BigDecimal.valueOf(pieces)).setScale(2));
        }

        recalculateCartPrice(cart);
    }

    @Transactional
    protected Order createOrder(Customer customer) {
        Cart cart = customer.getCart();

        if (cart.getOrderItems() == null || cart.getOrderItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty.");
        }

        if (customer.getBalance().compareTo(cart.getPrice()) < 0) {
            throw new IllegalArgumentException("Customer has insufficient balance.");
        }

        Customer managedCustomer = customerRepository.findById(customer.getId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found."));

        managedCustomer.setBalance(
                managedCustomer.getBalance().subtract(cart.getPrice()).setScale(2)
        );

        Order order = new Order();
        order.setCustomer(managedCustomer);
        order.setTimestampCreated(LocalDateTime.now());
        order.setPrice(cart.getPrice());

        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItem cartItem : cart.getOrderItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setFood(cartItem.getFood());
            orderItem.setPieces(cartItem.getPieces());
            orderItem.setPrice(cartItem.getPrice().setScale(2));
            orderItem.setOrder(order);
            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        customer.setBalance(managedCustomer.getBalance());
        customer.getCart().getOrderItems().clear();
        customer.getCart().setPrice(new BigDecimal("0.00"));

        return savedOrder;
    }

    private void recalculateCartPrice(Cart cart) {
        BigDecimal total = cart.getOrderItems()
                .stream()
                .map(OrderItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2);

        cart.setPrice(total);
    }
}
