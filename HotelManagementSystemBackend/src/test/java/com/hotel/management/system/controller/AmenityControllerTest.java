package com.hotel.management.system.controller;

import com.hotel.management.system.model.Amenity;
import com.hotel.management.system.repository.AmenityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AmenityController.class)
class AmenityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AmenityRepository amenityRepository;

    private Amenity buildAmenity(Long id, String name, String description) {
        Amenity amenity = new Amenity();
        amenity.setAmenityId(id);
        amenity.setName(name);
        amenity.setDescription(description);
        return amenity;
    }

    @Test
    @WithMockUser
    void getAllAmenities_returnsOkWithList() throws Exception {
        Amenity amenity = buildAmenity(1L, "Swimming Pool", "Outdoor pool");
        when(amenityRepository.findAll()).thenReturn(List.of(amenity));

        mockMvc.perform(get("/amenities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amenityId").value(1))
                .andExpect(jsonPath("$[0].name").value("Swimming Pool"))
                .andExpect(jsonPath("$[0].description").value("Outdoor pool"));
    }

    @Test
    @WithMockUser
    void getAllAmenities_returnsEmptyList_whenNoAmenities() throws Exception {
        when(amenityRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/amenities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser
    void getAllAmenities_returnsMultipleAmenities() throws Exception {
        Amenity a1 = buildAmenity(1L, "Swimming Pool", "Outdoor pool");
        Amenity a2 = buildAmenity(2L, "Gym", "Fully equipped gym");
        when(amenityRepository.findAll()).thenReturn(List.of(a1, a2));

        mockMvc.perform(get("/amenities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].name").value("Gym"));
    }
}
