package com.fooddelivery.converter;

import com.fooddelivery.domain.Cart;
import com.fooddelivery.domain.Order;
import com.fooddelivery.domain.OrderItem;
import com.fooddelivery.model.CartModel;
import com.fooddelivery.model.OrderItemModel;
import com.fooddelivery.model.OrderModel;
import com.fooddelivery.repository.FoodRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

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

    public static Cart toDomain(CartModel cartModel, FoodRepository foodRepository) {
        Cart cart = new Cart();
        cart.setPrice(cartModel.getPrice());

        List<OrderItem> orderItems = new ArrayList<>();
        if (cartModel.getOrderItemModels() != null) {
            for (OrderItemModel itemModel : cartModel.getOrderItemModels()) {
                OrderItem orderItem = new OrderItem();
                Long foodId = itemModel.getFoodModel().getId();
                orderItem.setFood(foodRepository.findById(foodId)
                        .orElseThrow(() -> new NoSuchElementException("Food not found: " + foodId)));
                orderItem.setPieces(itemModel.getPieces());
                orderItem.setPrice(itemModel.getPrice());
                orderItems.add(orderItem);
            }
        }
        cart.setOrderItems(orderItems);
        return cart;
    }
}
