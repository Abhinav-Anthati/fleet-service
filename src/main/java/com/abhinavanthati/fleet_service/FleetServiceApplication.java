package com.abhinavanthati.fleet_service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.abhinavanthati.fleet_service.entity.User;
import com.abhinavanthati.fleet_service.enums.UserRole;
import com.abhinavanthati.fleet_service.repository.UserRepository;

@SpringBootApplication
public class FleetServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FleetServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder encoder) {
		return args -> {
			if (userRepository.count() == 0) {
				User admin = new User();
				admin.setName("Admin");
				admin.setEmail("admin@fleet.com");
				admin.setPassword(encoder.encode("pass123"));
				admin.setRole(UserRole.ADMIN);
				userRepository.save(admin);
			}
		};
	}

}
