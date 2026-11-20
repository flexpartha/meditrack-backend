package com.bharath.meditrack.controller;

import com.bharath.meditrack.dto.DoctorRequest;
import com.bharath.meditrack.dto.DoctorResponse;
import com.bharath.meditrack.service.DoctorService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DoctorController.class)
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DoctorService doctorService;

    @Test
    void should_returnOkWithDoctorsList_when_getAllCalled() throws Exception {
        // Arrange
        DoctorResponse doctor = DoctorResponse.builder()
                .id(1L)
                .name("Dr. Smith")
                .licenseNo("DOC-001")
                .consultationFee(new BigDecimal("150.00"))
                .dailySlotCapacity(10)
                .active(true)
                .specialtyId(1L)
                .specialtyName("Cardiology")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(doctorService.getAll()).thenReturn(List.of(doctor));

        // Act & Assert
        mockMvc.perform(get("/api/v1/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Dr. Smith"))
                .andExpect(jsonPath("$[0].specialtyName").value("Cardiology"));
    }

    @Test
    void should_returnOkWithDoctor_when_getByIdFound() throws Exception {
        // Arrange
        DoctorResponse doctor = DoctorResponse.builder()
                .id(1L)
                .name("Dr. Smith")
                .licenseNo("DOC-001")
                .consultationFee(new BigDecimal("150.00"))
                .dailySlotCapacity(10)
                .active(true)
                .build();
        when(doctorService.getById(1L)).thenReturn(doctor);

        // Act & Assert
        mockMvc.perform(get("/api/v1/doctors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Dr. Smith"));
    }

    @Test
    void should_returnCreatedWithDoctor_when_validRequest() throws Exception {
        // Arrange
        DoctorRequest request = DoctorRequest.builder()
                .name("Dr. Adams")
                .licenseNo("DOC-002")
                .consultationFee(new BigDecimal("200.00"))
                .dailySlotCapacity(8)
                .active(true)
                .specialtyId(1L)
                .build();

        DoctorResponse response = DoctorResponse.builder()
                .id(2L)
                .name("Dr. Adams")
                .licenseNo("DOC-002")
                .consultationFee(new BigDecimal("200.00"))
                .dailySlotCapacity(8)
                .active(true)
                .specialtyId(1L)
                .specialtyName("Cardiology")
                .build();
        when(doctorService.create(any(DoctorRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Dr. Adams"));
    }
}
