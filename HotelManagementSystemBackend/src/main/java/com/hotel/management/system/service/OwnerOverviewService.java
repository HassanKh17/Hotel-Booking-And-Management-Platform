package com.hotel.management.system.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hotel.management.system.dto.OwnerHotelSummaryResponseDto;
import com.hotel.management.system.dto.OwnerOverviewResponseDto;
import com.hotel.management.system.dto.OwnerSummaryResponseDto;
import com.hotel.management.system.model.Hotel;
import com.hotel.management.system.model.HotelOwner;
import com.hotel.management.system.repository.HotelOwnerRepository;
import com.hotel.management.system.repository.HotelRepository;

@Service
public class OwnerOverviewService {

    private final HotelOwnerRepository hotelOwnerRepository;
    private final HotelRepository hotelRepository;

    public OwnerOverviewService(HotelOwnerRepository hotelOwnerRepository, HotelRepository hotelRepository) {
        this.hotelOwnerRepository = hotelOwnerRepository;
        this.hotelRepository = hotelRepository;
    }

    public OwnerOverviewResponseDto getOwnerOverview(Long ownerId) {
        HotelOwner owner = hotelOwnerRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Hotel owner not found with id: " + ownerId));

        OwnerSummaryResponseDto ownerSummary = new OwnerSummaryResponseDto(
                owner.getUserId(),
                owner.getUsername(),
                owner.getEmail(),
                owner.getBalance()
        );

        List<OwnerHotelSummaryResponseDto> hotels = hotelRepository.findByHotelOwnerUserId(ownerId)
                .stream()
                .map(this::mapHotelToSummary)
                .toList();

        return new OwnerOverviewResponseDto(ownerSummary, hotels);
    }

    private OwnerHotelSummaryResponseDto mapHotelToSummary(Hotel hotel) {
        return new OwnerHotelSummaryResponseDto(
                hotel.getHotelId(),
                hotel.getName(),
                hotel.getAddress().getCity(),
                hotel.getStarRating(),
                hotel.getSpecialOfferPercentage()
        );
    }
}