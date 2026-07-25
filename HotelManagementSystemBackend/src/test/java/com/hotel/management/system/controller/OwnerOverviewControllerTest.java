package com.hotel.management.system.controller;

import com.hotel.management.system.service.OwnerOverviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OwnerOverviewController.class)
class OwnerOverviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OwnerOverviewService ownerOverviewService;

    @Test
    void getOwnerOverview_returnsOk() throws Exception {
        when(ownerOverviewService.getOwnerOverview(3L)).thenReturn(null);

        mockMvc.perform(get("/admin/owners/3/overview"))
                .andExpect(status().isOk());

        verify(ownerOverviewService).getOwnerOverview(3L);
    }
}
