package com.bharath.meditrack.service;

import com.bharath.meditrack.dto.PatientRequest;
import com.bharath.meditrack.dto.PatientResponse;
import com.bharath.meditrack.exception.ResourceNotFoundException;
import com.bharath.meditrack.model.Patient;
import com.bharath.meditrack.repo.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepo;

    @InjectMocks
    private PatientService patientService;

    @Test
    void should_returnAllPatients_when_getAllCalled() {
        // Arrange
        Patient patient = Patient.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("555-1234")
                .dateOfBirth(LocalDate.of(1985, 5, 20))
                .createdAt(LocalDateTime.now())
                .build();
        when(patientRepo.findAll()).thenReturn(List.of(patient));

        // Act
        List<PatientResponse> result = patientService.getAll();

        // Assert
        assertThat(result)
                .hasSize(1)
                .first()
                .satisfies(res -> {
                    assertThat(res.getId()).isEqualTo(1L);
                    assertThat(res.getFirstName()).isEqualTo("John");
                    assertThat(res.getLastName()).isEqualTo("Doe");
                    assertThat(res.getEmail()).isEqualTo("john.doe@example.com");
                });
    }

    @Test
    void should_returnPatient_when_getByIdFound() {
        // Arrange
        Patient patient = Patient.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();
        when(patientRepo.findById(1L)).thenReturn(Optional.of(patient));

        // Act
        PatientResponse result = patientService.getById(1L);

        // Assert
        assertThat(result)
                .isNotNull()
                .satisfies(res -> {
                    assertThat(res.getId()).isEqualTo(1L);
                    assertThat(res.getFirstName()).isEqualTo("John");
                });
    }

    @Test
    void should_throwResourceNotFoundException_when_patientNotFoundById() {
        // Arrange
        when(patientRepo.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> patientService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Patient not found with id: 99");
    }

    @Test
    void should_registerAndReturnPatient_when_validRequestProvided() {
        // Arrange
        PatientRequest request = PatientRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .phone("555-5678")
                .dateOfBirth(LocalDate.of(1990, 8, 15))
                .build();

        Patient saved = Patient.builder()
                .id(2L)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .dateOfBirth(request.getDateOfBirth())
                .createdAt(LocalDateTime.now())
                .build();
        when(patientRepo.save(any(Patient.class))).thenReturn(saved);

        // Act
        PatientResponse result = patientService.register(request);

        // Assert
        assertThat(result)
                .isNotNull()
                .satisfies(res -> {
                    assertThat(res.getId()).isEqualTo(2L);
                    assertThat(res.getFirstName()).isEqualTo("Jane");
                    assertThat(res.getEmail()).isEqualTo("jane.smith@example.com");
                });
        verify(patientRepo).save(any(Patient.class));
    }
}
