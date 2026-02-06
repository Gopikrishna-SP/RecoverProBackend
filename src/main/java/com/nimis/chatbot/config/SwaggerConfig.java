package com.nimis.chatbot.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ✅ PRODUCTION-READY Swagger Configuration
 * - Disabled in production by default
 * - Enable via environment variable: SWAGGER_ENABLED=true
 */
@Slf4j
@Configuration
public class SwaggerConfig {

    @Value("${springdoc.swagger-ui.enabled:false}")
    private boolean swaggerEnabled;

    @Bean
    public OpenAPI chatbotOpenAPI() {
        if (!swaggerEnabled) {
            log.info("ℹ️ Swagger UI is DISABLED (set SWAGGER_ENABLED=true to enable)");
        } else {
            log.warn("⚠️ Swagger UI is ENABLED - ensure this is intentional in production!");
        }

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
                // 📘 API Info
                .info(new Info()
                        .title("Nimis Chatbot API")
                        .description("""
                                REST API for Nimis Chatbot application secured with JWT authentication.

                                ✅ Features:
                                - User authentication with role-based access control
                                - JWT token validation
                                - Secure endpoints for field executives, vendors, and bank admins
                                - File upload for allocations and visit logs
                                - Real-time notifications

                                ✅ Authentication Flow:
                                1. Call POST /api/auth/signin with email & password
                                2. Copy JWT token from response
                                3. Click **Authorize 🔓** button in Swagger UI
                                4. Enter: Bearer <YOUR_JWT_TOKEN>
                                5. Access secured endpoints

                                🔒 Security Notice:
                                - All endpoints (except /api/auth/**) require JWT authentication
                                - Tokens expire after 24 hours
                                - Use HTTPS in production
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Nimis Backend Team")
                                .email("support@nimis.com")
                        )
                        .license(new License()
                                .name("Proprietary")
                                .url("https://nimis.com/license")
                        )
                );
    }
}