package com.bharath.meditrack.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequest {
    private Long patientId;
    private Long doctorId;
    private LocalDate scheduledDate;
    private String notes;
}