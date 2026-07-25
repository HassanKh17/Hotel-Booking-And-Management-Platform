package com.hotel.management.system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.hotel.management.system.dto.OwnerOverviewResponseDto;
import com.hotel.management.system.service.OwnerOverviewService;

@RestController
@RequestMapping("/admin/owners")
public class OwnerOverviewController {

    private final OwnerOverviewService ownerOverviewService;

    public OwnerOverviewController(OwnerOverviewService ownerOverviewService) {
        this.ownerOverviewService = ownerOverviewService;
    }

    @GetMapping("/{ownerId}/overview")
    public OwnerOverviewResponseDto getOwnerOverview(@PathVariable Long ownerId) {
        return ownerOverviewService.getOwnerOverview(ownerId);
    }
}