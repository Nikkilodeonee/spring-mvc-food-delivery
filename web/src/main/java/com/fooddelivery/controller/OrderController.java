package com.fooddelivery.controller;

import com.fooddelivery.api.OrderServiceApi;
import com.fooddelivery.converter.OrderConverter;
import com.fooddelivery.domain.Cart;
import com.fooddelivery.domain.Order;
import com.fooddelivery.model.CartModel;
import com.fooddelivery.model.OrderModel;
import com.fooddelivery.service.FoodDeliveryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrderController implements OrderServiceApi {

    private final FoodDeliveryService foodDeliveryService;

    public OrderController(FoodDeliveryService foodDeliveryService) {
        this.foodDeliveryService = foodDeliveryService;
    }

    @Override
    public ResponseEntity<OrderModel> createOrder(CartModel cartModel) {
        Cart cart = OrderConverter.toDomain(cartModel);
        Order order = foodDeliveryService.createOrder(getAuthenticatedUserName(), cart);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderConverter.toModel(order));
    }

    @Override
    public ResponseEntity<OrderModel> getOrder(Long orderId) {
        Order order = foodDeliveryService.getOrderById(orderId, getAuthenticatedUserName());
        return ResponseEntity.ok(OrderConverter.toModel(order));
    }

    @Override
    public ResponseEntity<List<OrderModel>> getOrders() {
        List<OrderModel> orders = foodDeliveryService.getOrders(getAuthenticatedUserName()).stream()
                .map(OrderConverter::toModel)
                .toList();
        return ResponseEntity.ok(orders);
    }

    private String getAuthenticatedUserName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
