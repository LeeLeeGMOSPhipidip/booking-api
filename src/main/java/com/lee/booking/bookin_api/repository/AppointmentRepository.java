package com.lee.booking.bookin_api.repository;

import com.lee.booking.bookin_api.entity.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    @Query("""
        select a from AppointmentEntity a
        where a.status = com.lee.booking.bookin_api.entity.AppointmentEntity.Status.BOOKED
          and a.startTime < :newEnd
          and a.endTime > :newStart
    """)
    List<AppointmentEntity> findOverlaps(OffsetDateTime newStart, OffsetDateTime newEnd);

    List<AppointmentEntity> findByCustomerEmailOrderByStartTimeDesc(String customerEmail);
}
