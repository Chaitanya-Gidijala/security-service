package com.security.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title       = "Security Service — Authentication & Authorization API",
        version     = "v1.0",
        description = """
            Microservice providing:
            - Local JWT authentication (login / register)
            - OAuth2 Social Login (Google & GitHub)
            - User management (admin-only CRUD)
            - Token validation
            """,
        contact = @Contact(
            name  = "FindSharp Team",
            email = "security@findsharp.com"
        ),
        license = @License(name = "MIT License")
    ),
    servers = {
        @Server(url = "http://localhost:8085", description = "Local Development"),
        @Server(url = "https://security.findsharp.com", description = "Production")
    }
)
@SecurityScheme(
    name        = "bearerAuth",
    type        = SecuritySchemeType.HTTP,
    scheme      = "bearer",
    bearerFormat = "JWT",
    in          = SecuritySchemeIn.HEADER,
    description = "Paste the JWT token obtained from POST /api/v1/auth/login"
)
public class OpenApiConfig {
    // Configuration is fully annotation-driven — no bean methods needed
}
