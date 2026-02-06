package com.nimis.chatbot.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter
 * ✅ Production-grade with comprehensive error handling
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    /**
     * Filter incoming requests and set authentication if valid JWT is present
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {
        try {
            // Step 1: Extract token from Authorization header
            String token = jwtUtils.getJwtFromHeader(request);

            // Step 2: Validate token exists and is valid
            if (token == null) {
                log.debug("ℹ️ No JWT token found in request: {}", request.getRequestURI());
                chain.doFilter(request, response);
                return;
            }

            if (!jwtUtils.validateJwtToken(token)) {
                log.warn("⚠️ Invalid JWT token in request: {}", request.getRequestURI());
                chain.doFilter(request, response);
                return;
            }

            // Step 3: Extract username from valid token
            String username = jwtUtils.getUserNameFromJwtToken(token);

            if (username == null || username.isEmpty()) {
                log.warn("⚠️ Could not extract username from JWT token");
                chain.doFilter(request, response);
                return;
            }

            // Step 4: Load user details from database
            UserDetails userDetails;
            try {
                userDetails = userDetailsService.loadUserByUsername(username);
            } catch (UsernameNotFoundException ex) {
                log.warn("⚠️ User not found in database: {}", username);
                chain.doFilter(request, response);
                return;
            } catch (Exception ex) {
                log.error("❌ Error loading user details for username {}: {}", username, ex.getMessage());
                chain.doFilter(request, response);
                return;
            }

            // Step 5: Create authentication token
            try {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Step 6: Set authentication in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("✅ JWT authentication successful for user: {} | Endpoint: {}",
                        username, request.getRequestURI());

            } catch (Exception ex) {
                log.error("❌ Error creating authentication token: {}", ex.getMessage());
                SecurityContextHolder.clearContext();
            }

        } catch (Exception ex) {
            log.error("❌ Unexpected error in JWT authentication filter: {}", ex.getMessage(), ex);
            SecurityContextHolder.clearContext();
        }

        // Continue with filter chain
        try {
            chain.doFilter(request, response);
        } catch (Exception ex) {
            log.error("❌ Error in filter chain: {}", ex.getMessage());
            throw new ServletException("Filter chain error", ex);
        }
    }

    /**
     * Skip filter for certain requests (optional)
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // Don't filter these paths
        return path.startsWith("/api/auth/") ||
                path.startsWith("/public/") ||
                path.equals("/health");
    }
}