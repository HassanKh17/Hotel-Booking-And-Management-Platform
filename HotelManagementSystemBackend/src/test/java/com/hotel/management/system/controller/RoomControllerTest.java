package com.hotel.management.system.controller;

import com.hotel.management.system.dto.UnavailableDateRangeDto;
import com.hotel.management.system.mapper.RoomDtoMapper;
import com.hotel.management.system.model.OccupancyStatus;
import com.hotel.management.system.model.OccupancyType;
import com.hotel.management.system.model.Room;
import com.hotel.management.system.service.RoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomController.class)
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoomService roomService;

    @MockitoBean
    private RoomDtoMapper roomDtoMapper;

    @Test
    void addRoom_returnsOk() throws Exception {
        Room savedRoom = new Room(
                OccupancyType.DOUBLE,
                OccupancyStatus.AVAILABLE,
                new BigDecimal("120.00"),
                2);

        when(roomService.addRoom(eq(10L), any(Room.class))).thenReturn(savedRoom);
        when(roomDtoMapper.apply(savedRoom)).thenReturn(null);

        String requestBody = """
                {
                  "roomId": 1,
                  "occupancyType": "DOUBLE",
                  "occupancyStatus": "AVAILABLE",
                  "numberOfRooms": 5,
                  "pricePerNight": 120.00,
                  "capacity": 2
                }
                """;

        mockMvc.perform(post("/rooms/10")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());

        verify(roomService).addRoom(eq(10L), any(Room.class));
    }

    @Test
    void getRooms_returnsEmptyList() throws Exception {
        when(roomService.getRoomsByHotel(10L)).thenReturn(List.of());

        mockMvc.perform(get("/rooms/hotel/10"))
                .andExpect(status().isOk());

        verify(roomService).getRoomsByHotel(10L);
    }

    @Test
    void getRooms_returnsList() throws Exception {
        Room room = new Room(
                OccupancyType.DOUBLE,
                OccupancyStatus.AVAILABLE,
                new BigDecimal("120.00"),
                2);

        when(roomService.getRoomsByHotel(10L)).thenReturn(List.of(room));
        when(roomDtoMapper.apply(room)).thenReturn(null);

        mockMvc.perform(get("/rooms/hotel/10"))
                .andExpect(status().isOk());

        verify(roomService).getRoomsByHotel(10L);
    }

    @Test
    void getUnavailableDates_returnsOk() throws Exception {
        UnavailableDateRangeDto dto = new UnavailableDateRangeDto(
                LocalDate.of(2026, 4, 10),
                LocalDate.of(2026, 4, 12));

        when(roomService.getUnavailableDates(3L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/rooms/3/unavailable-dates"))
                .andExpect(status().isOk());

        verify(roomService).getUnavailableDates(3L);
    }
}