package com.fooddelivery.converter;

import com.fooddelivery.domain.Food;
import com.fooddelivery.model.FoodModel;

public final class FoodConverter {

    private FoodConverter() {
    }

    public static FoodModel toModel(Food food) {
        FoodModel model = new FoodModel(food.getId(), food.getName());
        model.setCalorie(food.getCalorie());
        model.setDescription(food.getDescription());
        model.setPrice(food.getPrice());
        return model;
    }
}
