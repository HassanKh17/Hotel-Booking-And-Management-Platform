package com.hotel.management.system.controller;

import com.hotel.management.system.dto.AmenityDto;
import com.hotel.management.system.repository.AmenityRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/amenities")
public class AmenityController {

    private final AmenityRepository amenityRepository;

    public AmenityController(AmenityRepository amenityRepository) {
        this.amenityRepository = amenityRepository;
    }

    @GetMapping
    public List<AmenityDto> getAllAmenities() {
        return amenityRepository.findAll()
                .stream()
                .map(a -> new AmenityDto(
                        a.getAmenityId(),
                        a.getName(),
                        a.getDescription()
                ))
                .toList();
    }
}