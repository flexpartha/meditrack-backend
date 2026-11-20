package com.bharath.meditrack.service;

import com.bharath.meditrack.dto.DoctorRequest;
import com.bharath.meditrack.dto.DoctorResponse;
import com.bharath.meditrack.exception.ResourceNotFoundException;
import com.bharath.meditrack.model.Doctor;
import com.bharath.meditrack.model.Specialty;
import com.bharath.meditrack.repo.DoctorRepository;
import com.bharath.meditrack.repo.SpecialtyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepo;
    private final SpecialtyRepository specialtyRepo;

    @Transactional(readOnly = true)
    public List<DoctorResponse> getAll() {
        return doctorRepo.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DoctorResponse getById(Long id) {
        Doctor doctor = doctorRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
        return toResponse(doctor);
    }

    @Transactional
    public DoctorResponse create(DoctorRequest request) {
        Specialty specialty = specialtyRepo.findById(request.getSpecialtyId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Specialty not found with id: " + request.getSpecialtyId()));

        Doctor doctor = Doctor.builder()
                .name(request.getName())
                .licenseNo(request.getLicenseNo())
                .consultationFee(request.getConsultationFee())
                .dailySlotCapacity(request.getDailySlotCapacity())
                .active(request.isActive())
                .specialty(specialty)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        doctor = doctorRepo.save(doctor);
        return toResponse(doctor);
    }

    private DoctorResponse toResponse(Doctor d) {
        return DoctorResponse.builder()
                .id(d.getId())
                .name(d.getName())
                .licenseNo(d.getLicenseNo())
                .consultationFee(d.getConsultationFee())
                .dailySlotCapacity(d.getDailySlotCapacity())
                .active(d.isActive())
                .specialtyId(d.getSpecialty() != null ? d.getSpecialty().getId() : null)
                .specialtyName(d.getSpecialty() != null ? d.getSpecialty().getName() : null)
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }
}
