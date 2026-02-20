package com.lee.booking.bookin_api.dto;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public class BookAppointmentRequest {

    @NotNull
    private Long serviceId;

    @NotNull
    private OffsetDateTime startTime;

    public Long getServiceId() { return serviceId; }
    public OffsetDateTime getStartTime() { return startTime; }

    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }
    public void setStartTime(OffsetDateTime startTime) { this.startTime = startTime; }
}
