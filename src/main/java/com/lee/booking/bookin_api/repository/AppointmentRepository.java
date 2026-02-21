package com.lee.booking.bookin_api.repository;

import com.lee.booking.bookin_api.entity.AppointmentEntity;
import com.lee.booking.bookin_api.entity.AppointmentEntity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

import java.time.OffsetDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    @Query("""
        select a
        from AppointmentEntity a
        where a.status = :status
          and a.startTime < :end
          and a.endTime > :start
        """)
    List<AppointmentEntity> findOverlaps(
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            @Param("status") Status status
    );

    List<AppointmentEntity> findByCustomerEmailOrderByStartTimeDesc(String customerEmail);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("""
    select a
    from AppointmentEntity a
    where a.status = :status
      and a.startTime < :end
      and a.endTime > :start
    """)
List<AppointmentEntity> findOverlapsForUpdate(
        @Param("start") OffsetDateTime start,
        @Param("end") OffsetDateTime end,
        @Param("status") Status status
);

}