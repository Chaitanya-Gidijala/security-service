package com.security.config;

import com.security.entity.AuthProvider;
import com.security.entity.Role;
import com.security.entity.User;
import com.security.repository.RoleRepository;
import com.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

/**
 * Initializes default roles (ROLE_USER, ROLE_ADMIN) and a default admin user
 * if they don't already exist in the database.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.default-admin.email}")
    private String adminEmail;

    @Value("${app.default-admin.password}")
    private String adminPassword;

    @Value("${app.super-admin.email}")
    private String superAdminEmail;

    @Value("${app.super-admin.password}")
    private String superAdminPassword;

    @Value("${app.default-user.email}")
    private String userEmail;

    @Value("${app.default-user.password}")
    private String userPassword;

    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Checking database for default roles and accounts...");
        initializeRoles();
        
        // Initialize or update Admin Account
        initializeAccount(adminEmail, adminPassword, "ROLE_ADMIN", "System Administrator", "admin");
        
        // Initialize or update Super Admin Account
        initializeAccount(superAdminEmail, superAdminPassword, "ROLE_ADMIN", "Super Administrator", "superadmin");
        
        // Initialize or update Standard User Account
        initializeAccount(userEmail, userPassword, "ROLE_USER", "Standard User", "user");
    }

    private void initializeRoles() {
        createRoleIfNotFound("ROLE_USER");
        createRoleIfNotFound("ROLE_ADMIN");
    }

    private Role createRoleIfNotFound(String roleName) {
        Optional<Role> roleOpt = roleRepository.findByName(roleName);
        if (roleOpt.isEmpty()) {
            Role role = new Role();
            role.setName(roleName);
            log.info("Created default role: {}", roleName);
            return roleRepository.save(role);
        }
        return roleOpt.get();
    }

    private void initializeAccount(String email, String password, String roleName, String displayName, String username) {
        if (!userRepository.existsByEmail(email)) {
            Role role = roleRepository.findByName(roleName)
                    .orElseGet(() -> createRoleIfNotFound(roleName));

            User user = User.builder()
                    .name(displayName)
                    .username(username)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .provider(AuthProvider.LOCAL)
                    .isActive(true)
                    .roles(new HashSet<>(Collections.singletonList(role)))
                    .build();
            userRepository.save(user);
            log.info("Created default account for {}: {}", roleName, email);
        } else {
            log.debug("Account already exists: {}", email);
        }
    }
}
