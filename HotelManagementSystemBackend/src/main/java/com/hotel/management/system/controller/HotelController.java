package com.hotel.management.system.controller;

import com.hotel.management.system.dto.HotelDto;
import com.hotel.management.system.mapper.HotelDtoMapper;
import com.hotel.management.system.model.Hotel;
import com.hotel.management.system.service.HotelService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/hotels")
public class HotelController {

    private final HotelService hotelService;
    private final HotelDtoMapper hotelDtoMapper;

    public HotelController(HotelService hotelService, HotelDtoMapper hotelDtoMapper) {
        this.hotelService = hotelService;
        this.hotelDtoMapper = hotelDtoMapper;
    }

    // CREATE
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HotelDto> createHotel(
            Authentication authentication,
            @RequestPart("hotel") Hotel hotel,
            @RequestPart(value = "amenityIds", required = false) List<Long> amenityIds,
            @RequestPart(value = "facilityIds", required = false) List<Long> facilityIds,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        String username = authentication.getName();
        Hotel savedHotel = hotelService.createHotel(username, hotel, amenityIds, facilityIds, image);
        return ResponseEntity.ok(hotelDtoMapper.apply(savedHotel));
    }

    // UPDATE
    @PutMapping(value = "/{hotelId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HotelDto> updateHotel(
            @PathVariable Long hotelId,
            Authentication authentication,
            @RequestPart("hotel") Hotel hotel,
            @RequestPart(value = "amenityIds", required = false) List<Long> amenityIds,
            @RequestPart(value = "facilityIds", required = false) List<Long> facilityIds,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        String username = authentication.getName();
        Hotel updatedHotel = hotelService.updateHotel(hotelId, username, hotel, amenityIds, facilityIds, image);
        return ResponseEntity.ok(hotelDtoMapper.apply(updatedHotel));
    }

    // DELETE
    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Void> deleteHotel(
            @PathVariable Long hotelId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        hotelService.deleteHotel(hotelId, username);
        return ResponseEntity.noContent().build();
    }

    // GET MY HOTELS (hotel owner sees only their own hotels)
    @GetMapping("/my-hotels")
    public ResponseEntity<List<HotelDto>> getMyHotels(Authentication authentication) {
        String username = authentication.getName();

        List<HotelDto> hotels = hotelService
                .getHotelsByOwnerUsername(username)
                .stream()
                .map(hotelDtoMapper)
                .toList();

        return ResponseEntity.ok(hotels);
    }

    // GET SINGLE HOTEL (public)
    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long hotelId) {
        return ResponseEntity.ok(
                hotelDtoMapper.apply(hotelService.getHotelById(hotelId))
        );
    }

    // GET ALL HOTELS (public)
    @GetMapping
    public ResponseEntity<List<HotelDto>> getAllHotels(@RequestParam(required = false) String city) {
        List<Hotel> hotels = (city != null && !city.isBlank())
                ? hotelService.getHotelsByCity(city)
                : hotelService.getAllHotels();

        return ResponseEntity.ok(hotels.stream().map(hotelDtoMapper).toList());
    }

}
