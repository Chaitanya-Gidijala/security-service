package com.security.controller;

import com.security.dto.LoginDto;
import com.security.dto.RegisterDto;
import com.security.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private AuthService authService;

    // Build Login REST API
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
        log.info("Login request received for user/email: {}", loginDto.getUsernameOrEmail());
        String response = authService.login(loginDto);
        log.info("Login successful for user/email: {}", loginDto.getUsernameOrEmail());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Build Register REST API
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        log.info("Register request received for user: {}, email: {}", registerDto.getUsername(),
                registerDto.getEmail());
        String response = authService.register(registerDto);
        log.info("Registration successful for user: {}", registerDto.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
