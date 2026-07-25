package com.hotel.management.system.dto;

import java.math.BigDecimal;

public class OwnerHotelSummaryResponseDto {

    private Long hotelId;
    private String name;
    private String city;
    private Integer starRating;
    private BigDecimal specialOfferPct;

    public OwnerHotelSummaryResponseDto() {
    }

    public OwnerHotelSummaryResponseDto(Long hotelId, String name, String city, Integer starRating, BigDecimal specialOfferPct) {
        this.hotelId = hotelId;
        this.name = name;
        this.city = city;
        this.starRating = starRating;
        this.specialOfferPct = specialOfferPct;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getStarRating() {
        return starRating;
    }

    public void setStarRating(Integer starRating) {
        this.starRating = starRating;
    }

    public BigDecimal getSpecialOfferPct() {
        return specialOfferPct;
    }

    public void setSpecialOfferPct(BigDecimal specialOfferPct) {
        this.specialOfferPct = specialOfferPct;
    }
}