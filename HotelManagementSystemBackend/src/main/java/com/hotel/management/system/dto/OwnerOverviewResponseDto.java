package com.hotel.management.system.dto;

import java.util.List;

public class OwnerOverviewResponseDto {

    private OwnerSummaryResponseDto owner;
    private List<OwnerHotelSummaryResponseDto> hotels;

    public OwnerOverviewResponseDto() {
    }

    public OwnerOverviewResponseDto(OwnerSummaryResponseDto owner, List<OwnerHotelSummaryResponseDto> hotels) {
        this.owner = owner;
        this.hotels = hotels;
    }

    public OwnerSummaryResponseDto getOwner() {
        return owner;
    }

    public void setOwner(OwnerSummaryResponseDto owner) {
        this.owner = owner;
    }

    public List<OwnerHotelSummaryResponseDto> getHotels() {
        return hotels;
    }

    public void setHotels(List<OwnerHotelSummaryResponseDto> hotels) {
        this.hotels = hotels;
    }
}