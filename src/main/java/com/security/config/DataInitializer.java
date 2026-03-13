package com.security.config;

import com.security.entity.Role;
import com.security.entity.User;
import com.security.repository.RoleRepository;
import com.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Ensure Roles exist
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_ADMIN")));

        roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_USER")));

        // 2. Create Admin user if it doesn't exist
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin@chaitanyatechworld.com");
            admin.setEmail("admin@chaitanyatechworld.com");
            admin.setPassword(passwordEncoder.encode("admin@chaitanyatechworld.com")); // Replace with a secure password
            admin.setActive(true);
            admin.setRoles(new HashSet<>(Collections.singletonList(adminRole)));

            userRepository.save(admin);
            System.out.println("Admin user created successfully.");
        }

        // 3. Fix existing inactive users (keeping your original logic)
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (!user.isActive()) {
                user.setActive(true);
                userRepository.save(user);
            }
        }
    }
}
