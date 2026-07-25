package com.hotel.management.system.controller;

import com.hotel.management.system.config.SecurityConfig;
import com.hotel.management.system.dto.AddressDto;
import com.hotel.management.system.dto.HotelDto;
import com.hotel.management.system.mapper.HotelDtoMapper;
import com.hotel.management.system.model.Hotel;
import com.hotel.management.system.service.HotelService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HotelController.class)
@Import(SecurityConfig.class)
class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HotelService hotelService;

    @MockitoBean
    private HotelDtoMapper hotelDtoMapper;

    private HotelDto buildHotelDto(Long id, String name, String city, String ownerUsername) {
        return new HotelDto(id, name, "A great hotel", 4, null, null,
                new AddressDto("1 Main St", city, "EC1A 1BB"),
                List.of(), List.of(), List.of(), ownerUsername);
    }

    @Test
    @WithMockUser
    void getAllHotels_returnsOkWithList() throws Exception {
        Hotel hotel = new Hotel();
        HotelDto dto = buildHotelDto(1L, "The Grand", "London", "owner_alice");
        when(hotelService.getAllHotels()).thenReturn(List.of(hotel));
        when(hotelDtoMapper.apply(hotel)).thenReturn(dto);

        mockMvc.perform(get("/hotels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("The Grand"));
    }

    @Test
    @WithMockUser
    void getAllHotels_returnsEmptyList_whenNoHotels() throws Exception {
        when(hotelService.getAllHotels()).thenReturn(List.of());

        mockMvc.perform(get("/hotels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser
    void getAllHotels_filterByCity_returnsMatchingHotels() throws Exception {
        Hotel hotel = new Hotel();
        HotelDto dto = buildHotelDto(1L, "The Grand", "London", "owner_alice");
        when(hotelService.getHotelsByCity("London")).thenReturn(List.of(hotel));
        when(hotelDtoMapper.apply(hotel)).thenReturn(dto);

        mockMvc.perform(get("/hotels").param("city", "London"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("The Grand"))
                .andExpect(jsonPath("$[0].address.city").value("London"));
    }

    @Test
    @WithMockUser
    void getAllHotels_filterByCity_returnsEmpty_whenNoMatch() throws Exception {
        when(hotelService.getHotelsByCity("Paris")).thenReturn(List.of());

        mockMvc.perform(get("/hotels").param("city", "Paris"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser
    void getHotelById_returnsOk() throws Exception {
        Hotel hotel = new Hotel();
        HotelDto dto = buildHotelDto(1L, "The Grand", "London", "owner_alice");
        when(hotelService.getHotelById(1L)).thenReturn(hotel);
        when(hotelDtoMapper.apply(hotel)).thenReturn(dto);

        mockMvc.perform(get("/hotels/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("The Grand"))
                .andExpect(jsonPath("$.starRating").value(4));
    }

    @Test
    @WithMockUser(username = "owner_alice")
    void getMyHotels_returnsOkWithOwnerHotels() throws Exception {
        Hotel hotel = new Hotel();
        HotelDto dto = buildHotelDto(1L, "The Grand", "London", "owner_alice");
        when(hotelService.getHotelsByOwnerUsername("owner_alice")).thenReturn(List.of(hotel));
        when(hotelDtoMapper.apply(hotel)).thenReturn(dto);

        mockMvc.perform(get("/hotels/my-hotels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("The Grand"))
                .andExpect(jsonPath("$[0].ownerUsername").value("owner_alice"));
    }

    @Test
    @WithMockUser(username = "owner_alice")
    void deleteHotel_returnsNoContent() throws Exception {
        doNothing().when(hotelService).deleteHotel(1L, "owner_alice");

        mockMvc.perform(delete("/hotels/1"))
                .andExpect(status().isNoContent());
    }
}
