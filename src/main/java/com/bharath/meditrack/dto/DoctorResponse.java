package com.bharath.meditrack.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponse {
    private Long id;
    private String name;
    private String licenseNo;
    private BigDecimal consultationFee;
    private int dailySlotCapacity;
    private boolean active;
    private Long specialtyId;
    private String specialtyName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}