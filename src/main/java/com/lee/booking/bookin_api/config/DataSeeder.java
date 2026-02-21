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
        seedIfMissing(repo, "Consultation", 30);
        seedIfMissing(repo, "Follow-up", 15);
        seedIfMissing(repo, "Extended Session", 60);
    };
}

private void seedIfMissing(ServiceRepository repo, String name, int duration) {
    if (!repo.existsByName(name)) {
        repo.save(new ServiceEntity(name, duration));
    }
}
}
