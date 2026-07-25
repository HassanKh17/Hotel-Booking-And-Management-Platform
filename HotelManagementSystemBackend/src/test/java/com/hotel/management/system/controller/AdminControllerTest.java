package com.hotel.management.system.controller;

import com.hotel.management.system.dto.HotelOwnerDto;
import com.hotel.management.system.service.AdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

    @Test
    void registerOwner_returnsCreated() throws Exception {
        HotelOwnerDto dto = new HotelOwnerDto(
                1L,
                "owner1",
                "owner@test.com",
                "password",
                new BigDecimal("10.00"));

        when(adminService.addOwner(any(HotelOwnerDto.class))).thenReturn(dto);

        String body = """
                {
                  "ownerId": 1,
                  "username": "owner1",
                  "email": "owner@test.com",
                  "password": "password",
                  "chargePercentage": 10.00
                }
                """;

        mockMvc.perform(post("/admin/register-owner")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());

        verify(adminService).addOwner(any(HotelOwnerDto.class));
    }

    @Test
    void editOwner_returnsOk() throws Exception {
        HotelOwnerDto dto = new HotelOwnerDto(
                7L,
                "owner2",
                "owner2@test.com",
                "secret",
                new BigDecimal("12.50"));

        when(adminService.editOwner(eq(7L), any(HotelOwnerDto.class))).thenReturn(dto);

        String body = """
                {
                  "ownerId": 7,
                  "username": "owner2",
                  "email": "owner2@test.com",
                  "password": "secret",
                  "chargePercentage": 12.50
                }
                """;

        mockMvc.perform(put("/admin/owners/7")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());

        verify(adminService).editOwner(eq(7L), any(HotelOwnerDto.class));
    }

    @Test
    void removeOwner_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/admin/owners/7").with(csrf()))
                .andExpect(status().isNoContent());

        verify(adminService).removeOwner(7L);
    }

    @Test
    void viewOwnerOverview_returnsOk() throws Exception {
        HotelOwnerDto dto = new HotelOwnerDto(
                1L,
                "owner1",
                "owner@test.com",
                "password",
                new BigDecimal("10.00"));

        when(adminService.viewOwnerOverview()).thenReturn(List.of(dto));

        mockMvc.perform(get("/admin/owners"))
                .andExpect(status().isOk());

        verify(adminService).viewOwnerOverview();
    }

    @Test
    void updateGlobalCharges_returnsOk() throws Exception {
        mockMvc.perform(patch("/admin/charges")
                .with(csrf())
                .param("percentage", "12.5"))
                .andExpect(status().isOk());

        verify(adminService).updateGlobalCharges(new BigDecimal("12.5"));
    }
}