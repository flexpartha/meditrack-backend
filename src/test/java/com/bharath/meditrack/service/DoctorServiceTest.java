package com.bharath.meditrack.service;

import com.bharath.meditrack.dto.DoctorRequest;
import com.bharath.meditrack.dto.DoctorResponse;
import com.bharath.meditrack.exception.ResourceNotFoundException;
import com.bharath.meditrack.model.Doctor;
import com.bharath.meditrack.model.Specialty;
import com.bharath.meditrack.repo.DoctorRepository;
import com.bharath.meditrack.repo.SpecialtyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepo;

    @Mock
    private SpecialtyRepository specialtyRepo;

    @InjectMocks
    private DoctorService doctorService;

    @Test
    void should_returnAllDoctors_when_getAllCalled() {
        // Arrange
        Specialty specialty = Specialty.builder().id(1L).name("Cardiology").build();
        Doctor doctor = Doctor.builder()
                .id(1L)
                .name("Dr. Smith")
                .licenseNo("DOC-001")
                .consultationFee(new BigDecimal("150.00"))
                .dailySlotCapacity(10)
                .active(true)
                .specialty(specialty)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(doctorRepo.findAll()).thenReturn(List.of(doctor));

        // Act
        List<DoctorResponse> result = doctorService.getAll();

        // Assert
        assertThat(result)
                .hasSize(1)
                .first()
                .satisfies(res -> {
                    assertThat(res.getId()).isEqualTo(1L);
                    assertThat(res.getName()).isEqualTo("Dr. Smith");
                    assertThat(res.getSpecialtyName()).isEqualTo("Cardiology");
                    assertThat(res.getConsultationFee()).isEqualByComparingTo(new BigDecimal("150.00"));
                });
    }

    @Test
    void should_returnDoctor_when_getByIdFound() {
        // Arrange
        Specialty specialty = Specialty.builder().id(1L).name("Cardiology").build();
        Doctor doctor = Doctor.builder()
                .id(1L)
                .name("Dr. Smith")
                .licenseNo("DOC-001")
                .consultationFee(new BigDecimal("150.00"))
                .dailySlotCapacity(10)
                .active(true)
                .specialty(specialty)
                .build();
        when(doctorRepo.findById(1L)).thenReturn(Optional.of(doctor));

        // Act
        DoctorResponse result = doctorService.getById(1L);

        // Assert
        assertThat(result)
                .isNotNull()
                .satisfies(res -> {
                    assertThat(res.getId()).isEqualTo(1L);
                    assertThat(res.getName()).isEqualTo("Dr. Smith");
                });
    }

    @Test
    void should_throwResourceNotFoundException_when_doctorNotFoundById() {
        // Arrange
        when(doctorRepo.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> doctorService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Doctor not found with id: 99");
    }

    @Test
    void should_createAndReturnDoctor_when_validRequestProvided() {
        // Arrange
        Specialty specialty = Specialty.builder().id(1L).name("Cardiology").build();
        DoctorRequest request = DoctorRequest.builder()
                .name("Dr. House")
                .licenseNo("DOC-002")
                .consultationFee(new BigDecimal("250.00"))
                .dailySlotCapacity(8)
                .active(true)
                .specialtyId(1L)
                .build();

        when(specialtyRepo.findById(1L)).thenReturn(Optional.of(specialty));

        Doctor savedDoctor = Doctor.builder()
                .id(2L)
                .name(request.getName())
                .licenseNo(request.getLicenseNo())
                .consultationFee(request.getConsultationFee())
                .dailySlotCapacity(request.getDailySlotCapacity())
                .active(request.isActive())
                .specialty(specialty)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(doctorRepo.save(any(Doctor.class))).thenReturn(savedDoctor);

        // Act
        DoctorResponse result = doctorService.create(request);

        // Assert
        assertThat(result)
                .isNotNull()
                .satisfies(res -> {
                    assertThat(res.getId()).isEqualTo(2L);
                    assertThat(res.getName()).isEqualTo("Dr. House");
                    assertThat(res.getSpecialtyName()).isEqualTo("Cardiology");
                });
        verify(doctorRepo).save(any(Doctor.class));
    }
}
