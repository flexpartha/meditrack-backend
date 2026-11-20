package com.bharath.meditrack.controller;

import com.bharath.meditrack.dto.AppointmentRequest;
import com.bharath.meditrack.dto.AppointmentResponse;
import com.bharath.meditrack.service.AppointmentBookingService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AppointmentBookingService appointmentBookingService;

    @Test
    void should_returnOkWithAppointmentsList_when_getAllCalled() throws Exception {
        // Arrange
        AppointmentResponse appointment = AppointmentResponse.builder()
                .appointmentId(1L)
                .appointmentNo("APT-12345678")
                .patientId(1L)
                .patientName("John Doe")
                .doctorId(1L)
                .doctorName("Dr. Smith")
                .status("REQUESTED")
                .scheduledDate(LocalDate.now().plusDays(1))
                .totalAmount(new BigDecimal("150.00"))
                .services(Collections.emptyList())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(appointmentBookingService.getAll()).thenReturn(List.of(appointment));

        // Act & Assert
        mockMvc.perform(get("/api/v1/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].appointmentId").value(1))
                .andExpect(jsonPath("$[0].appointmentNo").value("APT-12345678"))
                .andExpect(jsonPath("$[0].patientName").value("John Doe"))
                .andExpect(jsonPath("$[0].status").value("REQUESTED"));
    }

    @Test
    void should_returnOkWithAppointment_when_getByIdFound() throws Exception {
        // Arrange
        AppointmentResponse appointment = AppointmentResponse.builder()
                .appointmentId(1L)
                .appointmentNo("APT-12345678")
                .patientId(1L)
                .doctorId(1L)
                .status("REQUESTED")
                .services(Collections.emptyList())
                .build();
        when(appointmentBookingService.getById(1L)).thenReturn(appointment);

        // Act & Assert
        mockMvc.perform(get("/api/v1/appointments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointmentId").value(1))
                .andExpect(jsonPath("$.appointmentNo").value("APT-12345678"));
    }

    @Test
    void should_returnCreatedWithAppointment_when_bookedSuccessfully() throws Exception {
        // Arrange
        AppointmentRequest request = AppointmentRequest.builder()
                .patientId(1L)
                .doctorId(1L)
                .scheduledDate(LocalDate.now().plusDays(2))
                .notes("Routine checkup")
                .build();

        AppointmentResponse response = AppointmentResponse.builder()
                .appointmentId(10L)
                .appointmentNo("APT-87654321")
                .patientId(1L)
                .doctorId(1L)
                .status("REQUESTED")
                .scheduledDate(LocalDate.now().plusDays(2))
                .totalAmount(new BigDecimal("150.00"))
                .services(Collections.emptyList())
                .build();
        when(appointmentBookingService.book(any(AppointmentRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/appointments/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.appointmentId").value(10))
                .andExpect(jsonPath("$.status").value("REQUESTED"));
    }

    @Test
    void should_returnOkWithCancelledAppointment_when_cancelledSuccessfully() throws Exception {
        // Arrange
        AppointmentResponse response = AppointmentResponse.builder()
                .appointmentId(1L)
                .appointmentNo("APT-12345678")
                .status("CANCELLED")
                .services(Collections.emptyList())
                .build();
        when(appointmentBookingService.cancel(1L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/appointments/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointmentId").value(1))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
}
