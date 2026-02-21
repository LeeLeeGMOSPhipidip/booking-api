package com.lee.booking.bookin_api.service;

import com.lee.booking.bookin_api.dto.BookAppointmentRequest;
import com.lee.booking.bookin_api.entity.AppointmentEntity;
import com.lee.booking.bookin_api.entity.ServiceEntity;
import com.lee.booking.bookin_api.repository.AppointmentRepository;
import com.lee.booking.bookin_api.repository.ServiceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lee.booking.bookin_api.exception.ApiException;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepo;
    private final ServiceRepository serviceRepo;

    public AppointmentService(AppointmentRepository appointmentRepo, ServiceRepository serviceRepo) {
        this.appointmentRepo = appointmentRepo;
        this.serviceRepo = serviceRepo;
    }

    public List<AppointmentEntity> myAppointments(String customerEmail) {
        return appointmentRepo.findByCustomerEmailOrderByStartTimeDesc(customerEmail);
    }

    @Transactional
    public AppointmentEntity book(String customerEmail, BookAppointmentRequest req) {
        if (req.getStartTime().isBefore(OffsetDateTime.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Start time must be in the future");
        }

        ServiceEntity service = serviceRepo.findById(req.getServiceId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Service not found"));

        OffsetDateTime start = req.getStartTime();
        OffsetDateTime end = start.plusMinutes(service.getDurationMinutes());

        // Only block overlaps with BOOKED appointments (CANCELLED won't block)
        if (!appointmentRepo.findOverlapsForUpdate(start, end, AppointmentEntity.Status.BOOKED).isEmpty()) {
        throw new ApiException(HttpStatus.CONFLICT, "Time slot is not available");
}
        AppointmentEntity appt = new AppointmentEntity();
        appt.setCustomerEmail(customerEmail);
        appt.setService(service);
        appt.setStartTime(start);
        appt.setEndTime(end);
        appt.setStatus(AppointmentEntity.Status.BOOKED);

        return appointmentRepo.save(appt);
    }

    @Transactional
    public void cancel(String customerEmail, Long id) {
        AppointmentEntity appt = appointmentRepo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Appointment not found"));

        if (!appt.getCustomerEmail().equals(customerEmail)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your appointment");
        }

        appt.setStatus(AppointmentEntity.Status.CANCELLED);
        appointmentRepo.save(appt);
    }
}