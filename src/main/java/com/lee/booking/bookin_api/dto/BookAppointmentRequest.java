package com.lee.booking.bookin_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

@Schema(description = "Request body for booking an appointment")
public class BookAppointmentRequest {

    @NotNull
    @Schema(
        description = "ID of the service being booked",
        example = "1"
    )
    private Long serviceId;

    @NotNull
    @Schema(
        description = "Start date and time of the appointment, including timezone offset",
        example = "2026-02-21T10:30:00+02:00"
    )
    private OffsetDateTime startTime;

    public Long getServiceId() { return serviceId; }
    public OffsetDateTime getStartTime() { return startTime; }

    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }
    public void setStartTime(OffsetDateTime startTime) { this.startTime = startTime; }
}