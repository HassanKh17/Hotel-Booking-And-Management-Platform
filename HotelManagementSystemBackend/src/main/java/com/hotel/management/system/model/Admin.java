package com.hotel.management.system.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "admin")
public class Admin extends User {

    protected Admin() {
    }

    public Admin(String username, String email, String password) {
        super(username, email, password);
    }

    @Override
    protected String getAuthorityName() {
        return "ROLE_ADMIN";
    }
}
