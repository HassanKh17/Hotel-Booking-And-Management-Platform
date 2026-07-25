package com.hotel.management.system.controller;

import com.hotel.management.system.service.OwnerDashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OwnerDashboardController.class)
class OwnerDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OwnerDashboardService dashboardService;

    @Test
    @WithMockUser(username = "owner1")
    void getDashboard_returnsOk() throws Exception {
        when(dashboardService.getDashboard(eq("owner1"))).thenReturn(List.of());

        mockMvc.perform(get("/hotel-owner/occupancy-dashboard"))
                .andExpect(status().isOk());

        verify(dashboardService).getDashboard("owner1");
    }

    @Test
    void getDashboard_unauthenticated_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/hotel-owner/occupancy-dashboard"))
                .andExpect(status().isBadRequest());

        verify(dashboardService, never()).getDashboard("owner1");
    }
}