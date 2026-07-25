package com.hotel.management.system.mapper;

import com.hotel.management.system.dto.RoomDto;
import com.hotel.management.system.model.Hotel;
import com.hotel.management.system.model.Room;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Function;

@Service
public class RoomDtoMapper implements Function<Room, RoomDto> {

    public RoomDto apply(Room room) {
        BigDecimal originalPrice = room.getPricePerNight();
        Hotel hotel = room.getHotel();
        BigDecimal specialOfferPercentage = hotel == null
                ? BigDecimal.ZERO
                : normalisePercentage(hotel.getSpecialOfferPercentage());
        boolean specialOfferApplied = specialOfferPercentage.compareTo(BigDecimal.ZERO) > 0;
        BigDecimal discountedPrice = calculateDiscountedPrice(originalPrice, specialOfferPercentage);

        return new RoomDto(
                room.getRoomId(),
                room.getOccupancyType().name(),
                1,
                room.getOccupancyStatus().name(),
                room.getCapacity(),
                originalPrice,
                discountedPrice,
                specialOfferPercentage,
                specialOfferApplied
        );
    }

    private BigDecimal calculateDiscountedPrice(BigDecimal originalPrice, BigDecimal specialOfferPercentage) {
        if (originalPrice == null) {
            return BigDecimal.ZERO;
        }

        if (specialOfferPercentage.compareTo(BigDecimal.ZERO) <= 0) {
            return originalPrice;
        }

        BigDecimal discountMultiplier = BigDecimal.valueOf(100)
                .subtract(specialOfferPercentage)
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

        return originalPrice.multiply(discountMultiplier).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal normalisePercentage(BigDecimal specialOfferPercentage) {
        return specialOfferPercentage == null ? BigDecimal.ZERO : specialOfferPercentage;
    }
}
