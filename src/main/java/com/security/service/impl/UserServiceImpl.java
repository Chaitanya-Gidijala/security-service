package com.security.service.impl;

import com.security.dto.UpdateUserRequest;
import com.security.dto.UserDto;
import com.security.entity.Role;
import com.security.entity.User;
import com.security.exception.ResourceNotFoundException;
import com.security.repository.RoleRepository;
import com.security.repository.UserRepository;
import com.security.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Cacheable(value = "users")
    public List<UserDto> getAllUsers() {
        log.info("Fetching all users from database");
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "user", key = "#id")
    public UserDto getUserById(Long id) {
        log.info("Fetching user by id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return mapToDto(user);
    }

    @Override
    @Cacheable(value = "userByUsername", key = "#username")
    public UserDto getCurrentUser(String username) {
        log.info("Fetching profile for: {}", username);
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return mapToDto(user);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "users", allEntries = true),
            @CacheEvict(value = "user", key = "#id"),
            @CacheEvict(value = "userByUsername", allEntries = true)
    })
    public UserDto updateUser(Long id, UpdateUserRequest request) {
        log.info("Updating user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (request.getName()     != null) user.setName(request.getName());
        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getEmail()    != null) user.setEmail(request.getEmail());
        if (request.getActive()   != null) user.setActive(request.getActive());

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            Set<Role> roles = request.getRoles().stream()
                    .map(name -> roleRepository.findByName(name)
                            .orElseThrow(() -> new ResourceNotFoundException("Role", "name", name)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        User updated = userRepository.save(user);
        log.info("User {} updated successfully", id);
        return mapToDto(updated);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "users", allEntries = true),
            @CacheEvict(value = "user", key = "#id"),
            @CacheEvict(value = "userByUsername", allEntries = true)
    })
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
        log.info("User {} deleted", id);
    }

    @Override
    public long getUserCount() {
        return userRepository.count();
    }

    private UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .active(user.isActive())
                .provider(user.getProvider() != null ? user.getProvider().name() : "LOCAL")
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
