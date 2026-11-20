package com.bharath.meditrack.service;

import com.bharath.meditrack.dto.SpecialtyRequest;
import com.bharath.meditrack.dto.SpecialtyResponse;
import com.bharath.meditrack.exception.ResourceNotFoundException;
import com.bharath.meditrack.model.Specialty;
import com.bharath.meditrack.repo.SpecialtyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialtyService {

    private final SpecialtyRepository specialtyRepo;

    @Transactional(readOnly = true)
    public List<SpecialtyResponse> getAll() {
        return specialtyRepo.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public SpecialtyResponse create(SpecialtyRequest request) {
        Specialty specialty = Specialty.builder()
                .name(request.getName())
                .slug(request.getSlug())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        specialty = specialtyRepo.save(specialty);
        return toResponse(specialty);
    }

    private SpecialtyResponse toResponse(Specialty s) {
        return SpecialtyResponse.builder()
                .id(s.getId())
                .name(s.getName())
                .slug(s.getSlug())
                .description(s.getDescription())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }
}