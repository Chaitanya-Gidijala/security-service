package com.security.security.oauth2;

import com.security.entity.AuthProvider;
import com.security.entity.Role;
import com.security.entity.User;
import com.security.repository.RoleRepository;
import com.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Custom OAuth2 user service that intercepts the OAuth2 login flow for Google and GitHub.
 * It finds or creates a local User record for every social login.
 */
@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger log = LoggerFactory.getLogger(OAuth2UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public OAuth2UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        log.info("Processing OAuth2 login for provider: {}", registrationId);

        String email    = extractEmail(registrationId, attributes);
        String name     = extractName(registrationId, attributes);
        String providerId = extractProviderId(registrationId, attributes);

        if (email == null || email.isBlank()) {
            log.error("Email not provided by OAuth2 provider: {}", registrationId);
            throw new OAuth2AuthenticationException(new OAuth2Error("email_not_found"),
                    "Email not provided by " + registrationId + ". Please make your email public in your account settings.");
        }

        AuthProvider provider = AuthProvider.valueOf(registrationId.toUpperCase());
        User user = findOrCreateUser(email, name, provider, providerId);

        log.info("OAuth2 login successful for user: {} via {}", email, registrationId);
        return new OAuth2UserPrincipal(user, attributes);
    }

    // ── Private helpers ──────────────────────────────────────────────────────────

    private User findOrCreateUser(String email, String name, AuthProvider provider, String providerId) {
        return userRepository.findByEmail(email)
                .map(existingUser -> updateExistingOAuth2User(existingUser, name, provider, providerId))
                .orElseGet(() -> createNewOAuth2User(email, name, provider, providerId));
    }

    private User updateExistingOAuth2User(User user, String name, AuthProvider provider, String providerId) {
        boolean changed = false;
        // Only update provider info if still LOCAL (first social login)
        if (user.getProvider() == AuthProvider.LOCAL) {
            user.setProvider(provider);
            user.setProviderId(providerId);
            changed = true;
        }
        if (user.getName() == null && name != null) {
            user.setName(name);
            changed = true;
        }
        return changed ? userRepository.save(user) : user;
    }

    private User createNewOAuth2User(String email, String name, AuthProvider provider, String providerId) {
        Role userRole = roleRepository.findByName("ROLE_USER").orElseGet(() -> {
            Role r = new Role();
            r.setName("ROLE_USER");
            return roleRepository.save(r);
        });

        String username = generateUniqueUsername(email);

        User newUser = User.builder()
                .name(name)
                .username(username)
                .email(email)
                .provider(provider)
                .providerId(providerId)
                .isActive(true)
                .roles(new HashSet<>(Set.of(userRole)))
                .build();

        log.info("Creating new OAuth2 user: {} via {}", email, provider);
        return userRepository.save(newUser);
    }

    private String generateUniqueUsername(String email) {
        String base = email.split("@")[0].replaceAll("[^a-zA-Z0-9_]", "_");
        String username = base;
        int counter = 1;
        while (userRepository.existsByUsername(username)) {
            username = base + "_" + counter++;
        }
        return username;
    }

    private String extractEmail(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> (String) attributes.get("email");
            case "github" -> (String) attributes.get("email");
            default -> null;
        };
    }

    private String extractName(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> (String) attributes.get("name");
            case "github" -> {
                String name = (String) attributes.get("name");
                yield (name != null && !name.isBlank()) ? name : (String) attributes.get("login");
            }
            default -> null;
        };
    }

    private String extractProviderId(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> (String) attributes.get("sub");
            case "github" -> String.valueOf(attributes.get("id"));
            default -> null;
        };
    }
}
