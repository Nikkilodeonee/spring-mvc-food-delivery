package com.fooddelivery.controller;

import com.fooddelivery.api.FoodServiceApi;
import com.fooddelivery.converter.FoodConverter;
import com.fooddelivery.model.FoodModel;
import com.fooddelivery.service.FoodDeliveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FoodController implements FoodServiceApi {

    private final FoodDeliveryService foodDeliveryService;

    public FoodController(FoodDeliveryService foodDeliveryService) {
        this.foodDeliveryService = foodDeliveryService;
    }

    @Override
    public ResponseEntity<List<FoodModel>> getFoods() {
        List<FoodModel> foods = foodDeliveryService.getFoods().stream()
                .map(FoodConverter::toModel)
                .toList();
        return ResponseEntity.ok(foods);
    }
}
