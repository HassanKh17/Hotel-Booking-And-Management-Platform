package com.hotel.management.system.dto;

import java.math.BigDecimal;

public class OwnerSummaryResponseDto {

    private Long ownerId;
    private String username;
    private String email;
    private BigDecimal balance;

    public OwnerSummaryResponseDto() {
    }

    public OwnerSummaryResponseDto(Long ownerId, String username, String email, BigDecimal balance) {
        this.ownerId = ownerId;
        this.username = username;
        this.email = email;
        this.balance = balance;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}