package com.lee.booking.bookin_api.controller;

import com.lee.booking.bookin_api.entity.ServiceEntity;
import com.lee.booking.bookin_api.repository.ServiceRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services")
public class ServiceController {

    private final ServiceRepository serviceRepo;

    public ServiceController(ServiceRepository serviceRepo) {
        this.serviceRepo = serviceRepo;
    }

    @GetMapping
    public List<ServiceEntity> list() {
        return serviceRepo.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceEntity create(@Valid @RequestBody ServiceEntity service) {
        return serviceRepo.save(service);
    }
}