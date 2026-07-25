package com.hotel.management.system.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "hotel_owner")
public class HotelOwner extends User {

	 @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal balance;

    @OneToMany(mappedBy = "hotelOwner", cascade = CascadeType.ALL)
    private List<Hotel> hotels;

    public HotelOwner() {
    }

    public HotelOwner(String username, String email, String password, BigDecimal balance) {
        super(username, email, password);
        this.balance = balance;
    }

    @Override
    protected String getAuthorityName() {
        return "ROLE_HOTEL_OWNER";
    }

    public void addHotel(Hotel hotel) {
    }

    public void editHotel(Hotel hotel) {
    }

    public void removeHotel(Hotel hotel) {
    }

    public void viewOccupancy() {
    }

    public void viewAccountStatement() {
    }

    public void addSpecialOffer() {
    }

    public void replyToReview() {
    }

    public void deductBalance() {
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public List<Hotel> getHotels() {
        return hotels;
    }

    public void setHotels(List<Hotel> hotels) {
        this.hotels = hotels;
    }

}
