package com.hotel.management.system.controller;

import com.hotel.management.system.model.Facility;
import com.hotel.management.system.repository.FacilityRepository;
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

@WebMvcTest(FacilityController.class)
class FacilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FacilityRepository facilityRepository;

    private Facility buildFacility(Long id, String name, String description) {
        Facility facility = new Facility();
        facility.setFacilityId(id);
        facility.setName(name);
        facility.setDescription(description);
        return facility;
    }

    @Test
    @WithMockUser
    void getAllFacilities_returnsOkWithList() throws Exception {
        Facility facility = buildFacility(1L, "Conference Room", "Seats up to 50");
        when(facilityRepository.findAll()).thenReturn(List.of(facility));

        mockMvc.perform(get("/facilities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].facilityId").value(1))
                .andExpect(jsonPath("$[0].name").value("Conference Room"))
                .andExpect(jsonPath("$[0].description").value("Seats up to 50"));
    }

    @Test
    @WithMockUser
    void getAllFacilities_returnsEmptyList_whenNoFacilities() throws Exception {
        when(facilityRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/facilities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser
    void getAllFacilities_returnsMultipleFacilities() throws Exception {
        Facility f1 = buildFacility(1L, "Conference Room", "Seats up to 50");
        Facility f2 = buildFacility(2L, "Parking", "Free on-site parking");
        when(facilityRepository.findAll()).thenReturn(List.of(f1, f2));

        mockMvc.perform(get("/facilities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].name").value("Parking"));
    }
}
