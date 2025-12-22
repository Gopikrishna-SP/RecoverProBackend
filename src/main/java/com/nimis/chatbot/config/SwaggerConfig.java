package com.nimis.chatbot.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI chatbotOpenAPI() {
        return new OpenAPI()
                // 🔐 Enable JWT Bearer token support
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes(
                                "bearerAuth",
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                // 📘 API Info tab
                .info(new Info()
                        .title("Chatbot API")
                        .description("""
                                REST API for AI Chatbot application secured with JWT authentication.

                                ✅ Features:
                                - User authentication with role-based access
                                - JWT token validation
                                - Secure chatbot conversation endpoints
                                - Admin-level management endpoints

                                ✅ Authentication Flow:
                                1. Call /api/auth/signin with username & password.
                                2. Copy JWT token from response.
                                3. Click **Authorize 🔒** in Swagger.
                                4. Paste token as: Bearer <JWT_TOKEN>
                                5. Access secured APIs.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Chatbot Backend Team")
                                .email("support@chatbot.com")
                                .url("https://chatbot.com")
                        )
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")
                        )
                );
    }
}
