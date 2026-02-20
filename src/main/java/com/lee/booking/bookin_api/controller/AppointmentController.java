package com.lee.booking.bookin_api.controller;

import com.lee.booking.bookin_api.dto.BookAppointmentRequest;
import com.lee.booking.bookin_api.entity.AppointmentEntity;
import com.lee.booking.bookin_api.entity.ServiceEntity;
import com.lee.booking.bookin_api.repository.AppointmentRepository;
import com.lee.booking.bookin_api.repository.ServiceRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentRepository appointmentRepo;
    private final ServiceRepository serviceRepo;

    public AppointmentController(AppointmentRepository appointmentRepo, ServiceRepository serviceRepo) {
        this.appointmentRepo = appointmentRepo;
        this.serviceRepo = serviceRepo;
    }

    @GetMapping
    @Operation(
            summary = "List my appointments",
            description = "Returns appointments for the authenticated user (sorted by start time descending)."
    )
    @ApiResponse(responseCode = "200", description = "List returned")
    @ApiResponse(responseCode = "401", description = "Unauthorized (missing or invalid JWT)")
    public List<AppointmentEntity> myAppointments(Authentication auth) {
        String email = auth.getName();
        return appointmentRepo.findByCustomerEmailOrderByStartTimeDesc(email);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Book an appointment",
            description = "Books an appointment for the authenticated user. Validates serviceId, time in the future, and no overlaps."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Appointment created",
            content = @Content(schema = @Schema(implementation = AppointmentEntity.class))
    )
    @ApiResponse(responseCode = "400", description = "Bad Request (invalid body or start time in the past)")
    @ApiResponse(responseCode = "401", description = "Unauthorized (missing or invalid JWT)")
    @ApiResponse(responseCode = "404", description = "Service not found")
    @ApiResponse(responseCode = "409", description = "Time slot is not available (overlap)")
    public AppointmentEntity book(
            @Valid
            @org.springframework.web.bind.annotation.RequestBody
            BookAppointmentRequest req,
            Authentication auth
    ) {
        String email = auth.getName();

        if (req.getStartTime().isBefore(OffsetDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start time must be in the future");
        }

        ServiceEntity service = serviceRepo.findById(req.getServiceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found"));

        OffsetDateTime start = req.getStartTime();
        OffsetDateTime end = start.plusMinutes(service.getDurationMinutes());

        if (!appointmentRepo.findOverlaps(start, end).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Time slot is not available");
        }

        AppointmentEntity appt = new AppointmentEntity();
        appt.setCustomerEmail(email);
        appt.setService(service);
        appt.setStartTime(start);
        appt.setEndTime(end);
        appt.setStatus(AppointmentEntity.Status.BOOKED);

        return appointmentRepo.save(appt);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Cancel an appointment",
            description = "Marks the appointment as CANCELLED if it belongs to the authenticated user."
    )
    @ApiResponse(responseCode = "200", description = "Appointment cancelled")
    @ApiResponse(responseCode = "401", description = "Unauthorized (missing or invalid JWT)")
    @ApiResponse(responseCode = "403", description = "Forbidden (appointment does not belong to the user)")
    @ApiResponse(responseCode = "404", description = "Appointment not found")
    public void cancel(@PathVariable Long id, Authentication auth) {
        String email = auth.getName();

        AppointmentEntity appt = appointmentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        if (!appt.getCustomerEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your appointment");
        }

        appt.setStatus(AppointmentEntity.Status.CANCELLED);
        appointmentRepo.save(appt);
    }
}