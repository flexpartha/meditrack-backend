package com.bharath.meditrack.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentServiceResponse {
    private Long id;
    private String serviceName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}