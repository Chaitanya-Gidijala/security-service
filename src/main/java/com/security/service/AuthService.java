package com.security.service;

import com.security.dto.LoginDto;
import com.security.dto.RegisterDto;

public interface AuthService {
    String login(LoginDto loginDto);

    String register(RegisterDto registerDto);

    java.util.List<com.security.dto.UserDto> getAllUsers();

    com.security.dto.UserDto getUserById(Long id);

    void deleteUser(Long id);

    com.security.dto.UserDto updateUser(Long id, com.security.dto.UserDto userDto);

    long getUserCount();
}
