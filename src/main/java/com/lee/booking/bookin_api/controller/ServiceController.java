package com.lee.booking.bookin_api.controller;

import com.lee.booking.bookin_api.entity.ServiceEntity;
import com.lee.booking.bookin_api.repository.ServiceRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services")
public class ServiceController {

    private final ServiceRepository repo;

    public ServiceController(ServiceRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<ServiceEntity> getAll() {
        return repo.findAll();
    }
}
