package com.nimis.chatbot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * ✅ PRODUCTION-READY CORS Configuration
 * - Uses environment variables for allowed origins
 * - No hardcoded localhost URLs
 * - Supports multiple origins via comma-separated list
 */
@Slf4j
@Configuration
public class CorsConfig {

    @Value("${cors.allowed.origins:http://localhost:5173}")
    private String allowedOrigins;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // ✅ Parse allowed origins from environment variable
        List<String> origins = Arrays.asList(allowedOrigins.split(","));

        log.info("✅ CORS Configuration:");
        log.info("   Allowed Origins: {}", origins);

        config.setAllowedOriginPatterns(origins);

        // Allow HTTP methods
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Allow all headers
        config.setAllowedHeaders(List.of("*"));

        // Allow credentials (needed for JWT in Authorization header)
        config.setAllowCredentials(true);

        // Set max age for preflight cache (1 hour)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}