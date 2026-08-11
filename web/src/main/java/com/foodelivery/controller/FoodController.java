package com.foodelivery.controller;

import com.foodelivery.api.FoodServiceApi;
import com.foodelivery.converter.FoodConverter;
import com.foodelivery.model.FoodModel;
import com.foodelivery.service.FoodDeliveryService;
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
