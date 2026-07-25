package com.hotel.management.system.controller;

import com.hotel.management.system.dto.CustomerDto;
import com.hotel.management.system.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @Test
    void register_returnsCreated() throws Exception {
        when(customerService.addCustomer(any(CustomerDto.class))).thenReturn(null);

        String body = """
                {
                  "username": "nikita",
                  "email": "nikita@test.com",
                  "password": "secret"
                }
                """;

        mockMvc.perform(post("/customer/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());

        verify(customerService).addCustomer(any(CustomerDto.class));
    }
}