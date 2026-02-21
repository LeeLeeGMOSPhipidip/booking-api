package com.lee.booking.bookin_api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "services")
public class ServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    public ServiceEntity() {}

    public ServiceEntity(String name, Integer durationMinutes) {
        this.name = name;
        this.durationMinutes = durationMinutes;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Integer getDurationMinutes() { return durationMinutes; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
}