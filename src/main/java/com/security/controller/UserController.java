package com.security.controller;

import com.security.dto.UserDto;
import com.security.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@Slf4j
public class UserController {

    private AuthService authService;

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("Request received to fetch all users");
        List<UserDto> users = authService.getAllUsers();
        log.info("Successfully fetched {} users", users.size());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        log.info("Request received to fetch user with id: {}", id);
        UserDto user = authService.getUserById(id);
        log.info("Successfully fetched user with id: {}", id);
        return ResponseEntity.ok(user);
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        log.info("Request received to delete user with id: {}", id);
        authService.deleteUser(id);
        log.info("Successfully deleted user with id: {}", id);
        return ResponseEntity.ok("User deleted successfully!.");
    }

    @org.springframework.web.bind.annotation.PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id,
            @org.springframework.web.bind.annotation.RequestBody UserDto userDto) {
        log.info("Request received to update user with id: {}", id);
        UserDto updatedUser = authService.updateUser(id, userDto);
        log.info("Successfully updated user with id: {}", id);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getUserCount() {
        log.info("Request received to fetch user count");
        long count = authService.getUserCount();
        log.info("Successfully fetched user count: {}", count);
        return ResponseEntity.ok(count);
    }
}
