package com.lee.booking.bookin_api.controller;

import com.lee.booking.bookin_api.dto.BookAppointmentRequest;
import com.lee.booking.bookin_api.entity.AppointmentEntity;
import com.lee.booking.bookin_api.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    @Operation(
            summary = "List my appointments",
            description = "Returns appointments for the authenticated user (sorted by start time descending)."
    )
    @ApiResponse(responseCode = "200", description = "List returned")
    @ApiResponse(responseCode = "401", description = "Unauthorized (missing or invalid JWT)")
    public List<AppointmentEntity> myAppointments(Authentication auth) {
        return appointmentService.myAppointments(auth.getName());
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
            @Valid @RequestBody BookAppointmentRequest req,
            Authentication auth
    ) {
        return appointmentService.book(auth.getName(), req);
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
        appointmentService.cancel(auth.getName(), id);
    }
}