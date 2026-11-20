package com.bharath.meditrack.controller;

import com.bharath.meditrack.dto.SpecialtyRequest;
import com.bharath.meditrack.dto.SpecialtyResponse;
import com.bharath.meditrack.service.SpecialtyService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SpecialtyController.class)
class SpecialtyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SpecialtyService specialtyService;

    @Test
    void should_returnOkWithSpecialtiesList_when_getAllCalled() throws Exception {
        // Arrange
        SpecialtyResponse response = SpecialtyResponse.builder()
                .id(1L)
                .name("Cardiology")
                .slug("cardiology")
                .description("Cardiovascular")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(specialtyService.getAll()).thenReturn(List.of(response));

        // Act & Assert
        mockMvc.perform(get("/api/v1/specialties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Cardiology"));
    }

    @Test
    void should_returnCreatedWithSpecialty_when_validRequest() throws Exception {
        // Arrange
        SpecialtyRequest request = SpecialtyRequest.builder()
                .name("Cardiology")
                .slug("cardiology")
                .description("Cardiovascular")
                .build();
        SpecialtyResponse response = SpecialtyResponse.builder()
                .id(1L)
                .name("Cardiology")
                .slug("cardiology")
                .description("Cardiovascular")
                .build();
        when(specialtyService.create(any(SpecialtyRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/specialties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Cardiology"));
    }
}
