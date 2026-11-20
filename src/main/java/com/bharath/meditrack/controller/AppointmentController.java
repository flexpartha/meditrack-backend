package com.bharath.meditrack.controller;

import com.bharath.meditrack.dto.AppointmentRequest;
import com.bharath.meditrack.dto.AppointmentResponse;
import com.bharath.meditrack.service.AppointmentBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentBookingService appointmentBookingService;

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> getAll() {
        return ResponseEntity.ok(appointmentBookingService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentBookingService.getById(id));
    }

    @PostMapping("/book")
    public ResponseEntity<AppointmentResponse> book(@RequestBody AppointmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentBookingService.book(request));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentBookingService.cancel(id));
    }
}
