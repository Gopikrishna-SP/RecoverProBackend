package com.nimis.chatbot.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Authentication Entry Point
 * ✅ Production-grade error response handler
 */
@Slf4j
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    /**
     * Handle authentication failure
     * ✅ Proper error response with logging
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException ex
    ) throws IOException {
        try {
            log.warn("🔒 Unauthorized access attempt | Path: {} | IP: {} | Error: {}",
                    request.getRequestURI(),
                    getClientIp(request),
                    ex.getMessage());

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            Map<String, Object> body = new HashMap<>();
            body.put("status", 401);
            body.put("error", "Unauthorized");
            body.put("message", "Authentication failed: " + ex.getMessage());
            body.put("timestamp", System.currentTimeMillis());
            body.put("path", request.getServletPath());

            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), body);

            log.debug("✅ 401 Unauthorized response sent");

        } catch (IOException ioEx) {
            log.error("❌ Error writing authentication error response: {}", ioEx.getMessage(), ioEx);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        } catch (Exception unexpectedEx) {
            log.error("❌ Unexpected error in authentication entry point", unexpectedEx);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Extract client IP address from request
     * ✅ Handles proxy scenarios
     */
    private String getClientIp(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");

        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("Proxy-Client-IP");
        }

        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("WL-Proxy-Client-IP");
        }

        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        }

        // Handle comma-separated IPs (X-Forwarded-For)
        if (clientIp != null && clientIp.contains(",")) {
            clientIp = clientIp.split(",")[0].trim();
        }

        return clientIp;
    }
}