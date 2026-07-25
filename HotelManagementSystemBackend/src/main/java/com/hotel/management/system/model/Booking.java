package com.hotel.management.system.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    private BigDecimal totalCost;
    private LocalDate createdAt;

    // Maps the relationship to BookingItems
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL,
        orphanRemoval = true)
    private List<BookingItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<Transaction> transactions;

    // JPA requires a no-args constructor
    public Booking() {}

    public Booking(Customer customer, BigDecimal totalCost,
        LocalDate createdAt
    ) {
        this.customer = customer;
        this.totalCost = totalCost;
        this.createdAt = createdAt;
    }

    // Getters and setters

    public Long getId() { return id; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    
    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }
    
    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
    
    public List<BookingItem> getItems() { return items; }
    public void setItems(List<BookingItem> items) { this.items = items; }
    public void addBookingItem(BookingItem item) {
        // Maintain the bidirectional relationship
        items.add(item);
        item.setBooking(this);
    }
}
