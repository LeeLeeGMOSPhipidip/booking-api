package com.lee.booking.bookin_api.repository;

import com.lee.booking.bookin_api.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
}
