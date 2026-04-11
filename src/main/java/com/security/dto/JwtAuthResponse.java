package com.security.dto;

public class JwtAuthResponse {

    private String accessToken;
    private String tokenType = "Bearer";
    private long expiresIn;
    private UserDto user;

    public JwtAuthResponse() {
    }

    public JwtAuthResponse(String accessToken, String tokenType, long expiresIn, UserDto user) {
        this.accessToken = accessToken;
        this.tokenType = tokenType != null ? tokenType : "Bearer";
        this.expiresIn = expiresIn;
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public static JwtAuthResponseBuilder builder() {
        return new JwtAuthResponseBuilder();
    }

    public static class JwtAuthResponseBuilder {
        private String accessToken;
        private String tokenType = "Bearer";
        private long expiresIn;
        private UserDto user;

        public JwtAuthResponseBuilder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public JwtAuthResponseBuilder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public JwtAuthResponseBuilder expiresIn(long expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }

        public JwtAuthResponseBuilder user(UserDto user) {
            this.user = user;
            return this;
        }

        public JwtAuthResponse build() {
            return new JwtAuthResponse(accessToken, tokenType, expiresIn, user);
        }
    }
}
