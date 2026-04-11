package com.security.controller;

import com.security.dto.ApiResponse;
import com.security.dto.JwtAuthResponse;
import com.security.dto.LoginDto;
import com.security.dto.RegisterDto;
import com.security.dto.TokenValidationResponse;
import com.security.dto.ValidateTokenRequest;
import com.security.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Login, registration, and token management endpoints")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(
        summary = "User Login",
        description = "Authenticate with username/email and password. Returns a JWT Bearer token.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<ApiResponse<JwtAuthResponse>> login(@Valid @RequestBody LoginDto loginDto) {
        log.info("Login request for: {}", loginDto.getUsernameOrEmail());
        JwtAuthResponse authResponse = authService.login(loginDto);
        return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
    }

    @PostMapping("/register")
    @Operation(
        summary = "Register New User",
        description = "Create a new local user account. All fields are required and validated.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User registered"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Username or email already exists"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterDto registerDto) {
        log.info("Registration request for username: {}", registerDto.getUsername());
        ApiResponse<String> response = authService.register(registerDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/validate")
    @Operation(
        summary = "Validate JWT Token",
        description = "Check if a given JWT token is valid, not expired, and return the associated username.")
    public ResponseEntity<ApiResponse<TokenValidationResponse>> validateToken(
            @Valid @RequestBody ValidateTokenRequest request) {
        log.info("Token validation request");
        TokenValidationResponse validation = authService.validateToken(request.getToken());
        String message = validation.isValid() ? "Token is valid" : "Token is invalid or expired";
        return ResponseEntity.ok(ApiResponse.success(message, validation));
    }
}
