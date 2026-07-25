package com.hotel.management.system.mapper;

import com.hotel.management.system.dto.*;
import com.hotel.management.system.model.Amenity;
import com.hotel.management.system.model.Facility;
import com.hotel.management.system.model.Hotel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
public class HotelDtoMapper implements Function<Hotel, HotelDto> {

        private final RoomDtoMapper roomDtoMapper;
        private final AddressDtoMapper addressDtoMapper;

        public HotelDtoMapper(RoomDtoMapper roomDtoMapper,
                        AddressDtoMapper addressDtoMapper) {
                this.roomDtoMapper = roomDtoMapper;
                this.addressDtoMapper = addressDtoMapper;
        }

    @Override
    public HotelDto apply(Hotel hotel) {

        List<RoomDto> roomDtos = hotel.getRooms() == null
                ? List.of()
                : hotel.getRooms()
                .stream()
                .map(roomDtoMapper)
                .toList();

        AddressDto addressDto = hotel.getAddress() == null
                ? null
                : addressDtoMapper.apply(hotel.getAddress());

        List<FacilityDto> facilityDtos = hotel.getFacilities() == null
                ? List.of()
                : hotel.getFacilities().stream()
                .map(f -> new FacilityDto(
                        f.getFacilityId(),
                        f.getName(),
                        f.getDescription()
                ))
                .toList();

        List<AmenityDto> amenityDtos = hotel.getAmenities() == null
                ? List.of()
                : hotel.getAmenities().stream()
                .map(a -> new AmenityDto(
                        a.getAmenityId(),
                        a.getName(),
                        a.getDescription()
                ))
                .toList();

        return new HotelDto(
                hotel.getHotelId(),
                hotel.getName(),
                hotel.getDescription(),
                hotel.getStarRating(),
                hotel.getPhotoUrl(),
                hotel.getSpecialOfferPercentage(),
                addressDto,
                roomDtos,
                facilityDtos,
                amenityDtos,
                hotel.getHotelOwner() != null ? hotel.getHotelOwner().getUsername() : null);
}
}
