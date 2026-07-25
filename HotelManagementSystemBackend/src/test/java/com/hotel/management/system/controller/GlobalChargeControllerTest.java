package com.hotel.management.system.controller;

import com.hotel.management.system.dto.GlobalChargeRequest;
import com.hotel.management.system.service.GlobalChargeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GlobalChargeController.class)
class GlobalChargeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GlobalChargeService globalChargeService;

    @Test
    void getCurrent_returnsOk() throws Exception {
        when(globalChargeService.getCurrent()).thenReturn(null);

        mockMvc.perform(get("/admin/global-charges/current"))
                .andExpect(status().isOk());

        verify(globalChargeService).getCurrent();
    }

    @Test
    void getAll_returnsOk() throws Exception {
        when(globalChargeService.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/admin/global-charges"))
                .andExpect(status().isOk());

        verify(globalChargeService).getAll();
    }

    @Test
    void create_returnsCreated() throws Exception {
        when(globalChargeService.create(any(GlobalChargeRequest.class))).thenReturn(null);

        String body = """
                {
                  "baseMonthlyFee": 100.00,
                  "perRoomFee": 10.00,
                  "transactionFeePct": 5.00,
                  "effectiveFrom": "2026-04-02"
                }
                """;

        mockMvc.perform(post("/admin/global-charges")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());

        verify(globalChargeService).create(any(GlobalChargeRequest.class));
    }
}
