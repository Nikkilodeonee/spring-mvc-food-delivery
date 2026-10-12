package com.fooddelivery.converter;

import com.fooddelivery.domain.Cart;
import com.fooddelivery.domain.Food;
import com.fooddelivery.domain.Order;
import com.fooddelivery.domain.OrderItem;
import com.fooddelivery.model.CartItemModel;
import com.fooddelivery.model.CartModel;
import com.fooddelivery.model.OrderItemModel;
import com.fooddelivery.model.OrderModel;

import java.util.ArrayList;
import java.util.List;

public final class OrderConverter {

    private OrderConverter() {
    }

    public static OrderModel toModel(Order order) {
        OrderModel model = new OrderModel();
        model.setId(order.getOrderId());
        model.setPrice(order.getPrice());
        if (order.getTimestampCreated() != null) {
            model.setTimestampCreated(order.getTimestampCreated().toString());
        }

        List<OrderItemModel> itemModels = order.getOrderItems().stream()
                .map(OrderConverter::toItemModel)
                .toList();
        model.setOrderItemModels(itemModels);
        return model;
    }

    public static OrderItemModel toItemModel(OrderItem orderItem) {
        OrderItemModel model = new OrderItemModel();
        model.setId(orderItem.getId());
        model.setPieces(orderItem.getPieces());
        model.setPrice(orderItem.getPrice());
        model.setFoodModel(FoodConverter.toModel(orderItem.getFood()));
        return model;
    }

    public static Cart toDomain(CartModel cartModel) {
        Cart cart = new Cart();

        List<OrderItem> orderItems = new ArrayList<>();
        if (cartModel.getOrderItemModels() != null) {
            for (CartItemModel itemModel : cartModel.getOrderItemModels()) {
                if (itemModel == null || itemModel.getFoodId() == null || itemModel.getFoodId() <= 0) {
                    throw new IllegalArgumentException("A positive food ID is required.");
                }
                if (itemModel.getPieces() == null || itemModel.getPieces() <= 0) {
                    throw new IllegalArgumentException("Pieces must be positive.");
                }

                OrderItem orderItem = new OrderItem();
                Food food = new Food();
                food.setId(itemModel.getFoodId());
                orderItem.setFood(food);
                orderItem.setPieces(itemModel.getPieces());
                orderItems.add(orderItem);
            }
        }
        cart.setOrderItems(orderItems);
        return cart;
    }
}
