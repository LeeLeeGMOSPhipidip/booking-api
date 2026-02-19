package com.lee.booking.bookin_api.config;

import com.lee.booking.bookin_api.entity.ServiceEntity;
import com.lee.booking.bookin_api.repository.ServiceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedServices(ServiceRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                repo.save(new ServiceEntity("Consultation", 30));
                repo.save(new ServiceEntity("Follow-up", 15));
                repo.save(new ServiceEntity("Extended Session", 60));
            }
        };
    }
}
