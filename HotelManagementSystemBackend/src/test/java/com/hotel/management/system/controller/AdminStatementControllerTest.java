package com.hotel.management.system.controller;

import com.hotel.management.system.dto.OwnerStatementDto;
import com.hotel.management.system.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminStatementController.class)
class AdminStatementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @Test
    void getOwnerStatement_returnsOk() throws Exception {
        OwnerStatementDto dto = new OwnerStatementDto(
                5L,
                "owner1",
                "owner@test.com",
                new BigDecimal("100.00"),
                List.of());

        when(transactionService.getOwnerStatement(5L)).thenReturn(dto);

        mockMvc.perform(get("/admin/owners/5/statement"))
                .andExpect(status().isOk());

        verify(transactionService).getOwnerStatement(5L);
    }
}