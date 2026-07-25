package com.hotel.management.system.controller;

import com.hotel.management.system.dto.FacilityDto;
import com.hotel.management.system.repository.FacilityRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/facilities")
public class FacilityController {

    private final FacilityRepository facilityRepository;

    public FacilityController(FacilityRepository facilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    @GetMapping
    public List<FacilityDto> getAllFacilities() {
        return facilityRepository.findAll()
                .stream()
                .map(f -> new FacilityDto(
                        f.getFacilityId(),
                        f.getName(),
                        f.getDescription()
                ))
                .toList();
    }
}