package com.hotel.management.system.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.management.system.dto.HotelOccupancyDto;
import com.hotel.management.system.service.OwnerDashboardService;

@RestController
@RequestMapping("/hotel-owner")
public class OwnerDashboardController {

    private final OwnerDashboardService dashboardService;

    public OwnerDashboardController(OwnerDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/occupancy-dashboard")
    public ResponseEntity<List<HotelOccupancyDto>> getDashboard(
        Authentication authentication
    ) {
        String username = authentication.getName();
        return ResponseEntity.ok(
            dashboardService.getDashboard(username)
        );
    }
}
