package com.lee.booking.bookin_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class BookAppointmentRequest {

    @NotNull
    private LocalDate date;

    @NotNull
    private LocalTime time;

    @NotBlank
    private String service;

    @NotBlank
    private String customerName;

    private String notes;

    public LocalDate getDate() { return date; }
    public LocalTime getTime() { return time; }
    public String getService() { return service; }
    public String getCustomerName() { return customerName; }
    public String getNotes() { return notes; }

    public void setDate(LocalDate date) { this.date = date; }
    public void setTime(LocalTime time) { this.time = time; }
    public void setService(String service) { this.service = service; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setNotes(String notes) { this.notes = notes; }
}