package com.lee.booking.bookin_api.controller;

import com.lee.booking.bookin_api.dto.BookAppointmentRequest;
import com.lee.booking.bookin_api.entity.AppointmentEntity;
import com.lee.booking.bookin_api.entity.ServiceEntity;
import com.lee.booking.bookin_api.repository.AppointmentRepository;
import com.lee.booking.bookin_api.repository.ServiceRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;

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
    public List<AppointmentEntity> myAppointments(Authentication auth) {
        String email = auth.getName(); // from Spring Security (in-memory for now)
        return appointmentRepo.findByCustomerEmailOrderByStartTimeDesc(email);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentEntity book(@Valid @RequestBody BookAppointmentRequest req, Authentication auth) {
        String email = auth.getName();

        if (req.getStartTime().isBefore(OffsetDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start time must be in the future");
        }

        ServiceEntity service = serviceRepo.findById(req.getServiceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found"));

        OffsetDateTime start = req.getStartTime();
        OffsetDateTime end = start.plusMinutes(service.getDurationMinutes());

        // overlap check (single provider model)
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
