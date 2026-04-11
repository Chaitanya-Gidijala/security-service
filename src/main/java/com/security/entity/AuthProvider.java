package com.security.entity;

/**
 * Represents the authentication provider used by a user.
 * LOCAL = username/password, GOOGLE/GITHUB = OAuth2 social login.
 */
public enum AuthProvider {
    LOCAL,
    GOOGLE,
    GITHUB
}
