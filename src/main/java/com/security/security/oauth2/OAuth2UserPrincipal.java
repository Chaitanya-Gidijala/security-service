package com.security.security.oauth2;

import com.security.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Bridges the gap between OAuth2User (social login) and UserDetails (JWT flow).
 * A single principal type that works with both authentication paths.
 */
public class OAuth2UserPrincipal implements OAuth2User, UserDetails {

    private final User user;
    private Map<String, Object> attributes;

    /** Constructor for JWT-based authentication (no OAuth2 attributes needed) */
    public OAuth2UserPrincipal(User user) {
        this.user = user;
    }

    /** Constructor for OAuth2 social login */
    public OAuth2UserPrincipal(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    public User getUser() {
        return user;
    }

    // ── OAuth2User ──────────────────────────────────────────────────────────────

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /** The unique name Spring uses to identify this OAuth2 user (email) */
    @Override
    public String getName() {
        return user.getEmail();
    }

    // ── UserDetails ─────────────────────────────────────────────────────────────

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return user.isActive(); }
}
