package com.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health", description = "Application health check endpoint")
public class HealthController {

    @GetMapping
    @Operation(summary = "Check API Health", description = "Returns OK if the service is up and running")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
