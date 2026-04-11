package com.security.service;

import com.security.dto.ApiResponse;
import com.security.dto.JwtAuthResponse;
import com.security.dto.LoginDto;
import com.security.dto.RegisterDto;
import com.security.dto.TokenValidationResponse;
import com.security.dto.ValidateTokenRequest;

/**
 * Authentication service — handles login, registration, and token validation only.
 * User CRUD operations are handled by UserService.
 */
public interface AuthService {

    JwtAuthResponse login(LoginDto loginDto);

    ApiResponse<String> register(RegisterDto registerDto);

    TokenValidationResponse validateToken(String token);
}
