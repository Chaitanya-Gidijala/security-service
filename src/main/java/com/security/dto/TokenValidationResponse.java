package com.security.dto;

public class TokenValidationResponse {

    private boolean valid;
    private String username;
    private String message;

    public TokenValidationResponse() {
    }

    public TokenValidationResponse(boolean valid, String username, String message) {
        this.valid = valid;
        this.username = username;
        this.message = message;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static TokenValidationResponseBuilder builder() {
        return new TokenValidationResponseBuilder();
    }

    public static class TokenValidationResponseBuilder {
        private boolean valid;
        private String username;
        private String message;

        public TokenValidationResponseBuilder valid(boolean valid) {
            this.valid = valid;
            return this;
        }

        public TokenValidationResponseBuilder username(String username) {
            this.username = username;
            return this;
        }

        public TokenValidationResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public TokenValidationResponse build() {
            return new TokenValidationResponse(valid, username, message);
        }
    }
}
