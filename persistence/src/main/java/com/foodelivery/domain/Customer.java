package com.foodelivery.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
public class Customer extends Credentials {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigDecimal balance;

    @Transient
    private Cart cart = new Cart();

    public Customer() {
    }

    public Customer(Long id, String userName, String password, String name, BigDecimal balance) {
        super(userName, password);
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.cart = new Cart();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cart getCart() {
        if (cart == null) {
            cart = new Cart();
        }
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer customer)) return false;
        return Objects.equals(id, customer.id)
                && Objects.equals(getUserName(), customer.getUserName())
                && Objects.equals(getPassword(), customer.getPassword())
                && Objects.equals(name, customer.name)
                && Objects.equals(balance, customer.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, getUserName(), getPassword(), name, balance);
    }
}
