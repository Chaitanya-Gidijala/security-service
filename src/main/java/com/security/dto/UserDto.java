package com.security.dto;

import java.time.LocalDateTime;
import java.util.Set;

public class UserDto {

    private Long id;
    private String name;
    private String username;
    private String email;
    private boolean active;
    private String provider;
    private Set<String> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserDto() {
    }

    public UserDto(Long id, String name, String username, String email, boolean active, String provider, Set<String> roles, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.active = active;
        this.provider = provider;
        this.roles = roles;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static UserDtoBuilder builder() {
        return new UserDtoBuilder();
    }

    public static class UserDtoBuilder {
        private Long id;
        private String name;
        private String username;
        private String email;
        private boolean active;
        private String provider;
        private Set<String> roles;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public UserDtoBuilder id(Long id) { this.id = id; return this; }
        public UserDtoBuilder name(String name) { this.name = name; return this; }
        public UserDtoBuilder username(String username) { this.username = username; return this; }
        public UserDtoBuilder email(String email) { this.email = email; return this; }
        public UserDtoBuilder active(boolean active) { this.active = active; return this; }
        public UserDtoBuilder provider(String provider) { this.provider = provider; return this; }
        public UserDtoBuilder roles(Set<String> roles) { this.roles = roles; return this; }
        public UserDtoBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public UserDtoBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public UserDto build() {
            return new UserDto(id, name, username, email, active, provider, roles, createdAt, updatedAt);
        }
    }
}
