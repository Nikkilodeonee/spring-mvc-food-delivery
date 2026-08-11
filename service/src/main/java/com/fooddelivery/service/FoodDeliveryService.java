package com.fooddelivery.service;

import com.fooddelivery.domain.Cart;
import com.fooddelivery.domain.Credentials;
import com.fooddelivery.domain.Customer;
import com.fooddelivery.domain.Food;
import com.fooddelivery.domain.Order;

import java.util.List;

public interface FoodDeliveryService {

    Customer authenticate(Credentials credentials);

    List<Food> getFoods();

    List<Order> getOrders(String userName);

    Order getOrderById(long orderId, String userName);

    Order createOrder(String userName, Cart cart);

    void updateCart(Customer customer, Food food, int pieces);
}
