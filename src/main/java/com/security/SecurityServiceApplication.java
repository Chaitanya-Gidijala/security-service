package com.security;

import com.security.entity.Role;
import com.security.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SecurityServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(RoleRepository roleRepository) {
		return (args) -> {
			if (roleRepository.findByName("ROLE_USER").isEmpty()) {
				Role userRole = new Role();
				userRole.setName("ROLE_USER");
				roleRepository.save(userRole);
			}
			if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
				Role adminRole = new Role();
				adminRole.setName("ROLE_ADMIN");
				roleRepository.save(adminRole);
			}
		};
	}
}
