package com.security.controller;

import com.security.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/demo")
@Tag(name = "Demo API", description = "Endpoints to test different role-based access levels")
@SecurityRequirement(name = "bearerAuth")
public class DemoController {

    @GetMapping("/public")
    @Operation(summary = "Public Endpoint", description = "Accessible by anyone without authentication")
    public ResponseEntity<ApiResponse<String>> sayHello() {
        return ResponseEntity.ok(ApiResponse.success("Hello! This is a public endpoint."));
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "User Endpoint", description = "Accessible by anyone with a valid token and USER or ADMIN role")
    public ResponseEntity<ApiResponse<String>> sayHelloUser(@AuthenticationPrincipal UserDetails userDetails) {
        String message = String.format("Hello %s! You have accessed a protected USER endpoint.", userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin Endpoint", description = "Accessible ONLY by users with ADMIN role")
    public ResponseEntity<ApiResponse<String>> sayHelloAdmin(@AuthenticationPrincipal UserDetails userDetails) {
        String message = String.format("Hello Admin %s! You have accessed a highly protected ADMIN endpoint.", userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(message));
    }
}
