package com.security.service.impl;

import com.security.dto.LoginDto;
import com.security.dto.RegisterDto;
import com.security.entity.Role;
import com.security.entity.User;
import com.security.repository.RoleRepository;
import com.security.repository.UserRepository;
import com.security.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private com.security.security.JwtTokenProvider jwtTokenProvider;

    @Override
    public String login(LoginDto loginDto) {
        log.info("Attempting login for user/email: {}", loginDto.getUsernameOrEmail());
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsernameOrEmail(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);
        log.info("Login successful, token generated for user/email: {}", loginDto.getUsernameOrEmail());

        return token;
    }

    @Override
    public String register(RegisterDto registerDto) {
        log.info("Attempting registration for user: {}", registerDto.getUsername());

        // check for username exists in database
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            log.error("Registration failed: Username {} already exists", registerDto.getUsername());
            throw new RuntimeException("Username is already exists!.");
        }

        // check for email exists in database
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            log.error("Registration failed: Email {} already exists", registerDto.getEmail());
            throw new RuntimeException("Email is already exists!.");
        }

        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("ROLE_USER").get();
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);
        log.info("User registered successfully: {}", registerDto.getUsername());

        return "User registered successfully!.";
    }

    @Override
    public java.util.List<com.security.dto.UserDto> getAllUsers() {
        log.info("Fetching all users");
        java.util.List<User> users = userRepository.findAll();
        return users.stream().map(this::mapToDto).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public com.security.dto.UserDto getUserById(Long id) {
        log.info("Fetching user by id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", id);
                    return new RuntimeException("User not found with id: " + id);
                });
        return mapToDto(user);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Attempting to delete user with id: {}", id);
        if (!userRepository.existsById(id)) {
            log.error("User delete failed: User not found with id: {}", id);
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        log.info("User deleted successfully with id: {}", id);
    }

    @Override
    public com.security.dto.UserDto updateUser(Long id, com.security.dto.UserDto userDto) {
        log.info("Attempting to update user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User update failed: User not found with id: {}", id);
                    return new RuntimeException("User not found with id: " + id);
                });

        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setActive(userDto.isActive());

        if (userDto.getRoles() != null) {
            Set<Role> roles = userDto.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                    .collect(java.util.stream.Collectors.toSet());
            user.setRoles(roles);
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with id: {}", id);
        return mapToDto(updatedUser);
    }

    @Override
    public long getUserCount() {
        log.info("Fetching total user count");
        long count = userRepository.count();
        log.info("Total users found: {}", count);
        return count;
    }

    private com.security.dto.UserDto mapToDto(User user) {
        com.security.dto.UserDto userDto = new com.security.dto.UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setActive(user.isActive());
        userDto.setRoles(user.getRoles().stream().map(Role::getName).collect(java.util.stream.Collectors.toSet()));
        return userDto;
    }
}
