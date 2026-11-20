package com.bharath.meditrack.service;

import com.bharath.meditrack.dto.AppointmentRequest;
import com.bharath.meditrack.dto.AppointmentResponse;
import com.bharath.meditrack.dto.AppointmentServiceResponse;
import com.bharath.meditrack.exception.ResourceNotFoundException;
import com.bharath.meditrack.model.Appointment;
import com.bharath.meditrack.model.AppointmentService;
import com.bharath.meditrack.model.Doctor;
import com.bharath.meditrack.model.Patient;
import com.bharath.meditrack.repo.AppointmentRepository;
import com.bharath.meditrack.repo.DoctorRepository;
import com.bharath.meditrack.repo.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentBookingService {

    private final AppointmentRepository appointmentRepo;
    private final DoctorRepository doctorRepo;
    private final PatientRepository patientRepo;

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAll() {
        return appointmentRepo.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AppointmentResponse getById(Long id) {
        Appointment appointment = appointmentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
        return toResponse(appointment);
    }

    @Transactional
    public AppointmentResponse book(AppointmentRequest request) {
        Patient patient = patientRepo.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with id: " + request.getPatientId()));

        Doctor doctor = doctorRepo.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found with id: " + request.getDoctorId()));

        long booked = appointmentRepo.countByDoctorAndScheduledDate(doctor, request.getScheduledDate());
        if (booked >= doctor.getDailySlotCapacity()) {
            throw new IllegalStateException(
                    "No slots available for this doctor on " + request.getScheduledDate());
        }

        Appointment appointment = Appointment.builder()
                .appointmentNo("APT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .patient(patient)
                .doctor(doctor)
                .status("REQUESTED")
                .scheduledDate(request.getScheduledDate())
                .totalAmount(doctor.getConsultationFee())
                .notes(request.getNotes())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        appointment = appointmentRepo.save(appointment);
        return toResponse(appointment);
    }

    @Transactional
    public AppointmentResponse cancel(Long id) {
        Appointment appointment = appointmentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
        appointment.setStatus("CANCELLED");
        appointment.setUpdatedAt(LocalDateTime.now());
        appointment = appointmentRepo.save(appointment);
        return toResponse(appointment);
    }

    private AppointmentResponse toResponse(Appointment a) {
        List<AppointmentServiceResponse> serviceResponses =
                a.getServices() != null
                        ? a.getServices().stream().map(this::toServiceResponse).toList()
                        : Collections.emptyList();

        return AppointmentResponse.builder()
                .appointmentId(a.getAppointmentId())
                .appointmentNo(a.getAppointmentNo())
                .patientId(a.getPatient() != null ? a.getPatient().getId() : null)
                .patientName(a.getPatient() != null
                        ? a.getPatient().getFirstName() + " " + a.getPatient().getLastName()
                        : null)
                .doctorId(a.getDoctor() != null ? a.getDoctor().getId() : null)
                .doctorName(a.getDoctor() != null ? a.getDoctor().getName() : null)
                .status(a.getStatus())
                .scheduledDate(a.getScheduledDate())
                .totalAmount(a.getTotalAmount())
                .notes(a.getNotes())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .services(serviceResponses)
                .build();
    }

    private AppointmentServiceResponse toServiceResponse(AppointmentService s) {
        return AppointmentServiceResponse.builder()
                .id(s.getId())
                .serviceName(s.getServiceName())
                .quantity(s.getQuantity())
                .unitPrice(s.getUnitPrice())
                .subtotal(s.getSubtotal())
                .build();
    }
}
