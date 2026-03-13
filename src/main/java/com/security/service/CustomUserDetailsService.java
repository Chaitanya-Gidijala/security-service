package com.security.service;

import com.security.entity.User;
import com.security.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

        private UserRepository userRepository;

        @Override
        public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
                log.info("Loading user by username or email: {}", usernameOrEmail);

                User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                                .orElseThrow(() -> {
                                        log.error("User not found with username or email: {}", usernameOrEmail);
                                        return new UsernameNotFoundException("User not exists by Username or Email");
                                });

                Set<GrantedAuthority> authorities = user.getRoles().stream()
                                .map((role) -> new SimpleGrantedAuthority(role.getName()))
                                .collect(Collectors.toSet());

                log.info("User loaded successfully: {}", usernameOrEmail);
                return new org.springframework.security.core.userdetails.User(
                                usernameOrEmail,
                                user.getPassword(),
                                authorities);
        }
}
