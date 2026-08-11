package com.fooddelivery.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigDecimal calorie;
    private String description;
    private BigDecimal price;

    public Food() {
    }

    public Food(Long id, String name, BigDecimal calorie, String description, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.calorie = calorie;
        this.description = description;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getCalorie() {
        return calorie;
    }

    public void setCalorie(BigDecimal calorie) {
        this.calorie = calorie;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    private BigDecimal normalized(BigDecimal value) {
        return value == null ? null : value.setScale(2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Food food)) return false;
        return Objects.equals(id, food.id)
                && Objects.equals(name, food.name)
                && Objects.equals(normalized(calorie), normalized(food.calorie))
                && Objects.equals(description, food.description)
                && Objects.equals(normalized(price), normalized(food.price));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, normalized(calorie), description, normalized(price));
    }
}
