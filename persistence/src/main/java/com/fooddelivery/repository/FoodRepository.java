package com.fooddelivery.repository;

import com.fooddelivery.domain.Food;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, Long> {
}
