package com.foodelivery.service;

import com.foodelivery.domain.Cart;
import com.foodelivery.domain.Credentials;
import com.foodelivery.domain.Customer;
import com.foodelivery.domain.Food;
import com.foodelivery.domain.Order;

import java.util.List;

public interface FoodDeliveryService {

    Customer authenticate(Credentials credentials);

    List<Food> getFoods();

    List<Order> getOrders(String userName);

    Order getOrderById(long orderId, String userName);

    Order createOrder(String userName, Cart cart);

    void updateCart(Customer customer, Food food, int pieces);
}
