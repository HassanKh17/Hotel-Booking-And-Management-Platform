package com.hotel.management.system.controller;

import com.hotel.management.system.dto.BookingResponseDto;
import com.hotel.management.system.dto.CustomerBookingsDto;
import com.hotel.management.system.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingService bookingService;

    @Test
    @WithMockUser(username = "testuser")
    void addBooking_ReturnsCreated() throws Exception {
        BookingResponseDto responseDto = new BookingResponseDto(
                1L,
                10L,
                new BigDecimal("200.00"),
                LocalDate.now(),
                List.of()
        );

        when(bookingService.addBooking(any(), anyString()))
                .thenReturn(responseDto);

        String requestBody = """
                {
                  "cardholderName": "Nikita",
                  "cardNumber": "1234567812345678",
                  "expiry": "12/26",
                  "cvv": "123",
                  "address": null,
                  "items": []
                }
                """;

        mockMvc.perform(post("/bookings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingId").value(1));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getMyBookings_ReturnsOk() throws Exception {
        CustomerBookingsDto bookingsDto = new CustomerBookingsDto(List.of(), List.of());

        when(bookingService.getMyBookings(anyString()))
                .thenReturn(bookingsDto);

        mockMvc.perform(get("/bookings/me"))
                .andExpect(status().isOk());
    }
}