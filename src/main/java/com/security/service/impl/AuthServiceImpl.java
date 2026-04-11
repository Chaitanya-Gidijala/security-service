package com.security.service.impl;

import com.security.dto.ApiResponse;
import com.security.dto.JwtAuthResponse;
import com.security.dto.LoginDto;
import com.security.dto.RegisterDto;
import com.security.dto.TokenValidationResponse;
import com.security.dto.UserDto;
import com.security.entity.AuthProvider;
import com.security.entity.Role;
import com.security.entity.User;
import com.security.exception.UserAlreadyExistsException;
import com.security.repository.RoleRepository;
import com.security.repository.UserRepository;
import com.security.security.JwtTokenProvider;
import com.security.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public JwtAuthResponse login(LoginDto loginDto) {
        log.info("Processing login for: {}", loginDto.getUsernameOrEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsernameOrEmail(),
                        loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);

        User user = userRepository
                .findByUsernameOrEmail(loginDto.getUsernameOrEmail(), loginDto.getUsernameOrEmail())
                .orElseThrow(() -> new RuntimeException("User not found after authentication"));

        log.info("Login successful for: {}", loginDto.getUsernameOrEmail());

        return JwtAuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpiresInMs())
                .user(mapToUserDto(user))
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<String> register(RegisterDto registerDto) {
        log.info("Processing registration for username: {}", registerDto.getUsername());

        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new UserAlreadyExistsException(
                    "Username '" + registerDto.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new UserAlreadyExistsException(
                    "Email '" + registerDto.getEmail() + "' is already registered");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException(
                        "Default role ROLE_USER not found. Please contact the administrator."));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = User.builder()
                .name(registerDto.getName())
                .username(registerDto.getUsername())
                .email(registerDto.getEmail())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .provider(AuthProvider.LOCAL)
                .isActive(true)
                .roles(roles)
                .build();

        userRepository.save(user);
        log.info("User registered successfully: {}", registerDto.getUsername());

        return ApiResponse.success(
                "Registration successful! Please login to continue.",
                registerDto.getUsername());
    }

    @Override
    public TokenValidationResponse validateToken(String token) {
        boolean valid = jwtTokenProvider.validateToken(token);
        if (valid) {
            String username = jwtTokenProvider.getUsernameFromToken(token);
            return TokenValidationResponse.builder()
                    .valid(true)
                    .username(username)
                    .message("Token is valid")
                    .build();
        }
        return TokenValidationResponse.builder()
                .valid(false)
                .message("Token is invalid or expired")
                .build();
    }

    private UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .active(user.isActive())
                .provider(user.getProvider().name())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
