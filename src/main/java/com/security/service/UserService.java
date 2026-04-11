package com.security.service;

import com.security.dto.UpdateUserRequest;
import com.security.dto.UserDto;

import java.util.List;

/**
 * User management service — handles CRUD operations on users.
 * Authentication is handled by AuthService.
 */
public interface UserService {

    /** Returns all users. Accessible only by ADMIN. */
    List<UserDto> getAllUsers();

    /** Returns a user by their database ID. Accessible only by ADMIN. */
    UserDto getUserById(Long id);

    /** Returns the profile of the currently authenticated user */
    UserDto getCurrentUser(String username);

    /** Updates a user's profile (admin operation) */
    UserDto updateUser(Long id, UpdateUserRequest request);

    /** Deletes a user permanently (admin operation) */
    void deleteUser(Long id);

    /** Returns total number of registered users */
    long getUserCount();
}
