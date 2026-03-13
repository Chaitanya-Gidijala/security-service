package com.security.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
@Slf4j
public class DemoController {

    @GetMapping
    public ResponseEntity<String> demo() {
        log.info("Demo endpoint accessed");
        return ResponseEntity.ok("Hello! You have successfully accessed a protected resource using JWT.");
    }
}
