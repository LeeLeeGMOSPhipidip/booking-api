package com.lee.booking.bookin_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lee.booking.bookin_api.dto.BookAppointmentRequest;
import com.lee.booking.bookin_api.entity.AppointmentEntity;
import com.lee.booking.bookin_api.entity.ServiceEntity;
import com.lee.booking.bookin_api.repository.AppointmentRepository;
import com.lee.booking.bookin_api.repository.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AppointmentControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private ServiceRepository serviceRepo;
    @Autowired private AppointmentRepository appointmentRepo;

    private ServiceEntity service;

    @BeforeEach
    void setup() {
        appointmentRepo.deleteAll();
        serviceRepo.deleteAll();

        ServiceEntity s = new ServiceEntity();
        s.setName("Consultation");
        s.setDurationMinutes(30);
        service = serviceRepo.save(s);
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void book_overlap_cancel_rebook() throws Exception {
        OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC)
                .plusDays(2)
                .withSecond(0)
                .withNano(0);

        // 1) Book -> 201
        BookAppointmentRequest req1 = new BookAppointmentRequest();
        req1.setServiceId(service.getId());
        req1.setStartTime(start);

        String createdJson = mockMvc.perform(post("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req1)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        AppointmentEntity created = objectMapper.readValue(createdJson, AppointmentEntity.class);

        // 2) Overlapping book -> 409
        BookAppointmentRequest req2 = new BookAppointmentRequest();
        req2.setServiceId(service.getId());
        req2.setStartTime(start.plusMinutes(10));

        mockMvc.perform(post("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req2)))
                .andExpect(status().isConflict());

        // 3) Cancel -> 200
        mockMvc.perform(delete("/appointments/" + created.getId()))
                .andExpect(status().isOk());

        // 4) Rebook same time -> 201 (assuming CANCELLED does not block overlaps)
        mockMvc.perform(post("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req1)))
                .andExpect(status().isCreated());
    }
}