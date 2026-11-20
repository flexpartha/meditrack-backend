package com.bharath.meditrack.controller;

import com.bharath.meditrack.dto.PatientRequest;
import com.bharath.meditrack.dto.PatientResponse;
import com.bharath.meditrack.service.PatientService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PatientService patientService;

    @Test
    void should_returnOkWithPatientsList_when_getAllCalled() throws Exception {
        // Arrange
        PatientResponse patient = PatientResponse.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("555-1234")
                .dateOfBirth(LocalDate.of(1985, 5, 20))
                .createdAt(LocalDateTime.now())
                .build();
        when(patientService.getAll()).thenReturn(List.of(patient));

        // Act & Assert
        mockMvc.perform(get("/api/v1/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"));
    }

    @Test
    void should_returnCreatedWithPatient_when_validRequest() throws Exception {
        // Arrange
        PatientRequest request = PatientRequest.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .phone("555-5678")
                .dateOfBirth(LocalDate.of(1992, 11, 10))
                .build();

        PatientResponse response = PatientResponse.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .phone("555-5678")
                .dateOfBirth(LocalDate.of(1992, 11, 10))
                .createdAt(LocalDateTime.now())
                .build();
        when(patientService.register(any(PatientRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.email").value("jane.doe@example.com"));
    }
}
