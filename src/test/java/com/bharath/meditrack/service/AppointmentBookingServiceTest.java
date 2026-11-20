package com.bharath.meditrack.service;

import com.bharath.meditrack.dto.AppointmentRequest;
import com.bharath.meditrack.dto.AppointmentResponse;
import com.bharath.meditrack.exception.ResourceNotFoundException;
import com.bharath.meditrack.model.Appointment;
import com.bharath.meditrack.model.Doctor;
import com.bharath.meditrack.model.Patient;
import com.bharath.meditrack.repo.AppointmentRepository;
import com.bharath.meditrack.repo.DoctorRepository;
import com.bharath.meditrack.repo.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentBookingServiceTest {

    @Mock
    private AppointmentRepository appointmentRepo;

    @Mock
    private DoctorRepository doctorRepo;

    @Mock
    private PatientRepository patientRepo;

    @InjectMocks
    private AppointmentBookingService appointmentBookingService;

    @Test
    void should_returnAllAppointments_when_getAllCalled() {
        // Arrange
        Patient patient = Patient.builder().id(1L).firstName("John").lastName("Doe").build();
        Doctor doctor = Doctor.builder().id(1L).name("Dr. Smith").build();
        Appointment appointment = Appointment.builder()
                .appointmentId(1L)
                .appointmentNo("APT-12345678")
                .patient(patient)
                .doctor(doctor)
                .status("REQUESTED")
                .scheduledDate(LocalDate.now().plusDays(1))
                .totalAmount(new BigDecimal("150.00"))
                .services(Collections.emptyList())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(appointmentRepo.findAll()).thenReturn(List.of(appointment));

        // Act
        List<AppointmentResponse> result = appointmentBookingService.getAll();

        // Assert
        assertThat(result)
                .hasSize(1)
                .first()
                .satisfies(res -> {
                    assertThat(res.getAppointmentId()).isEqualTo(1L);
                    assertThat(res.getAppointmentNo()).isEqualTo("APT-12345678");
                    assertThat(res.getPatientName()).isEqualTo("John Doe");
                    assertThat(res.getDoctorName()).isEqualTo("Dr. Smith");
                    assertThat(res.getStatus()).isEqualTo("REQUESTED");
                });
    }

    @Test
    void should_bookAppointment_when_slotsAvailable() {
        // Arrange
        LocalDate date = LocalDate.now().plusDays(2);
        Patient patient = Patient.builder().id(1L).firstName("John").lastName("Doe").build();
        Doctor doctor = Doctor.builder()
                .id(1L)
                .name("Dr. Smith")
                .dailySlotCapacity(5)
                .consultationFee(new BigDecimal("150.00"))
                .build();

        AppointmentRequest request = AppointmentRequest.builder()
                .patientId(1L)
                .doctorId(1L)
                .scheduledDate(date)
                .notes("Routine checkup")
                .build();

        when(patientRepo.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepo.findById(1L)).thenReturn(Optional.of(doctor));
        when(appointmentRepo.countByDoctorAndScheduledDate(doctor, date)).thenReturn(2L);

        Appointment saved = Appointment.builder()
                .appointmentId(10L)
                .appointmentNo("APT-ABCD1234")
                .patient(patient)
                .doctor(doctor)
                .status("REQUESTED")
                .scheduledDate(date)
                .totalAmount(new BigDecimal("150.00"))
                .notes("Routine checkup")
                .services(Collections.emptyList())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(appointmentRepo.save(any(Appointment.class))).thenReturn(saved);

        // Act
        AppointmentResponse result = appointmentBookingService.book(request);

        // Assert
        assertThat(result)
                .isNotNull()
                .satisfies(res -> {
                    assertThat(res.getAppointmentId()).isEqualTo(10L);
                    assertThat(res.getStatus()).isEqualTo("REQUESTED");
                    assertThat(res.getTotalAmount()).isEqualByComparingTo(new BigDecimal("150.00"));
                });
        verify(appointmentRepo).save(any(Appointment.class));
    }

    @Test
    void should_throwIllegalStateException_when_noSlotsAvailable() {
        // Arrange
        LocalDate date = LocalDate.now().plusDays(2);
        Patient patient = Patient.builder().id(1L).build();
        Doctor doctor = Doctor.builder()
                .id(1L)
                .dailySlotCapacity(3)
                .build();

        AppointmentRequest request = AppointmentRequest.builder()
                .patientId(1L)
                .doctorId(1L)
                .scheduledDate(date)
                .build();

        when(patientRepo.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepo.findById(1L)).thenReturn(Optional.of(doctor));
        when(appointmentRepo.countByDoctorAndScheduledDate(doctor, date)).thenReturn(3L);

        // Act & Assert
        assertThatThrownBy(() -> appointmentBookingService.book(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No slots available");
    }

    @Test
    void should_cancelAppointment_when_appointmentExists() {
        // Arrange
        Appointment appointment = Appointment.builder()
                .appointmentId(1L)
                .status("REQUESTED")
                .services(Collections.emptyList())
                .build();
        when(appointmentRepo.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepo.save(any(Appointment.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        AppointmentResponse result = appointmentBookingService.cancel(1L);

        // Assert
        assertThat(result)
                .isNotNull()
                .satisfies(res -> assertThat(res.getStatus()).isEqualTo("CANCELLED"));
        verify(appointmentRepo).save(any(Appointment.class));
    }

    @Test
    void should_throwResourceNotFoundException_when_cancelingNonExistentAppointment() {
        // Arrange
        when(appointmentRepo.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> appointmentBookingService.cancel(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Appointment not found with id: 999");
    }
}
