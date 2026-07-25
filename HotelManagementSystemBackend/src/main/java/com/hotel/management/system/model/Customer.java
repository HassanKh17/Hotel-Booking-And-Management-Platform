package com.hotel.management.system.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer")
public class Customer extends User {

    protected Customer() {}

    public Customer(String username, String email, String password) {
        super(username, email, password);
    }

    @Override
    protected String getAuthorityName() {
        return "ROLE_CUSTOMER";
    }
}
