package com.lee.booking.bookin_api.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "appointments")
public class AppointmentEntity {

    public enum Status { BOOKED, CANCELLED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // store the customer identity (email) for now; later we’ll link to User table
    @Column(nullable = false)
    private String customerEmail;

    @ManyToOne(optional = false)
    @JoinColumn(name = "service_id")
    private ServiceEntity service;

    @Column(nullable = false)
    private OffsetDateTime startTime;

    @Column(nullable = false)
    private OffsetDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.BOOKED;

    public AppointmentEntity() {}

    public Long getId() { return id; }
    public String getCustomerEmail() { return customerEmail; }
    public ServiceEntity getService() { return service; }
    public OffsetDateTime getStartTime() { return startTime; }
    public OffsetDateTime getEndTime() { return endTime; }
    public Status getStatus() { return status; }

    public void setId(Long id) { this.id = id; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public void setService(ServiceEntity service) { this.service = service; }
    public void setStartTime(OffsetDateTime startTime) { this.startTime = startTime; }
    public void setEndTime(OffsetDateTime endTime) { this.endTime = endTime; }
    public void setStatus(Status status) { this.status = status; }
}
