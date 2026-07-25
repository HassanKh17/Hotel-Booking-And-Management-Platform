package com.hotel.management.system.model;

import jakarta.persistence.*;

@Entity
public class SavedCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cardId;

    private String cardholderName;
    
    @Column(columnDefinition = "TEXT")
    private String encryptedCardNumber;
    
    private String lastFourDigits;
    private String expiryDate;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address billingAddress;

    // Constructors
    public SavedCard() {}

    public SavedCard(
        String cardholderName,
        String encryptedCardNumber,
        String lastFourDigits,
        String expiryDate,
        Customer customer,
        Address billingAddress
    ) {
        this.cardholderName = cardholderName;
        this.encryptedCardNumber = encryptedCardNumber;
        this.lastFourDigits = lastFourDigits;
        this.expiryDate = expiryDate;
        this.customer = customer;
        this.billingAddress = billingAddress;
    }
    
    // Getters and setters

    public Long getCardId() {
        return cardId;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public String getEncryptedCardNumber() {
        return encryptedCardNumber;
    }

    public void setEncryptedCardNumber(String encryptedCardNumber) {
        this.encryptedCardNumber = encryptedCardNumber;
    }

    public String getLastFourDigits() {
        return lastFourDigits;
    }

    public void setLastFourDigits(String lastFourDigits) {
        this.lastFourDigits = lastFourDigits;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }
}