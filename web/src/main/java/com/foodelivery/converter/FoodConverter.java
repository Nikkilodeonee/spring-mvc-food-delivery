package com.foodelivery.converter;

import com.foodelivery.domain.Food;
import com.foodelivery.model.FoodModel;

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
