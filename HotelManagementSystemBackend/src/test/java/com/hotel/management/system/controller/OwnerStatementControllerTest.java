package com.hotel.management.system.controller;

import com.hotel.management.system.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OwnerStatementController.class)
class OwnerStatementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @Test
    @WithMockUser(username = "owner1")
    void getMyStatement_returnsOk() throws Exception {
        when(transactionService.getOwnerStatementByUsername(eq("owner1"))).thenReturn(null);

        mockMvc.perform(get("/hotel-owner/statement"))
                .andExpect(status().isOk());

        verify(transactionService).getOwnerStatementByUsername("owner1");
    }

    @Test
    void getMyStatement_unauthenticated_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/hotel-owner/statement"))
                .andExpect(status().isUnauthorized());

        verify(transactionService, never()).getOwnerStatementByUsername("owner1");
    }
}