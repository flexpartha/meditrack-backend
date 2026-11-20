package com.bharath.meditrack.service;

import com.bharath.meditrack.dto.SpecialtyRequest;
import com.bharath.meditrack.dto.SpecialtyResponse;
import com.bharath.meditrack.model.Specialty;
import com.bharath.meditrack.repo.SpecialtyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpecialtyServiceTest {

    @Mock
    private SpecialtyRepository specialtyRepo;

    @InjectMocks
    private SpecialtyService specialtyService;

    @Test
    void should_returnAllSpecialties_when_getAllCalled() {
        // Arrange
        Specialty specialty = Specialty.builder()
                .id(1L)
                .name("Cardiology")
                .slug("cardiology")
                .description("Heart and cardiovascular system")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(specialtyRepo.findAll()).thenReturn(List.of(specialty));

        // Act
        List<SpecialtyResponse> result = specialtyService.getAll();

        // Assert
        assertThat(result)
                .hasSize(1)
                .first()
                .satisfies(res -> {
                    assertThat(res.getId()).isEqualTo(1L);
                    assertThat(res.getName()).isEqualTo("Cardiology");
                    assertThat(res.getSlug()).isEqualTo("cardiology");
                });
    }

    @Test
    void should_createAndReturnSpecialty_when_validRequestProvided() {
        // Arrange
        SpecialtyRequest request = SpecialtyRequest.builder()
                .name("Neurology")
                .slug("neurology")
                .description("Brain and nervous system")
                .build();

        Specialty saved = Specialty.builder()
                .id(2L)
                .name(request.getName())
                .slug(request.getSlug())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(specialtyRepo.save(any(Specialty.class))).thenReturn(saved);

        // Act
        SpecialtyResponse result = specialtyService.create(request);

        // Assert
        assertThat(result).isNotNull()
                .satisfies(res -> {
                    assertThat(res.getId()).isEqualTo(2L);
                    assertThat(res.getName()).isEqualTo("Neurology");
                    assertThat(res.getSlug()).isEqualTo("neurology");
                });
        verify(specialtyRepo).save(any(Specialty.class));
    }
}
