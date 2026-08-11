package com.foodelivery.repository;

import com.foodelivery.domain.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"orderItems", "orderItems.food", "customer"})
    List<Order> findByCustomer_UserName(String userName);

    @Override
    @EntityGraph(attributePaths = {"orderItems", "orderItems.food", "customer"})
    Optional<Order> findById(Long id);
}
