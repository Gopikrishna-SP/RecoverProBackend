package com.nimis.chatbot.security.jwt;

import com.nimis.chatbot.model.entity.UserEntity;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtils {

    @Value("${spring.app.jwtSecret:#{null}}")
    private String jwtSecret;

    @Value("${spring.app.jwtExpirationMs:86400000}")
    private long jwtExpirationMs;

    /**
     * Extract JWT token from Authorization header
     * ✅ Proper Bearer token validation
     */
    public String getJwtFromHeader(HttpServletRequest request) {
        try {
            String header = request.getHeader("Authorization");

            if (header == null || header.isEmpty()) {
                log.debug("⚠️ No Authorization header found");
                return null;
            }

            if (!header.startsWith("Bearer ")) {
                log.warn("⚠️ Invalid Authorization header format: {}", header.substring(0, Math.min(10, header.length())));
                return null;
            }

            String token = header.substring(7).trim();

            if (token.isEmpty()) {
                log.warn("⚠️ Bearer token is empty");
                return null;
            }

            log.debug("✅ JWT token extracted from header");
            return token;

        } catch (Exception ex) {
            log.error("❌ Error extracting JWT from header: {}", ex.getMessage());
            return null;
        }
    }

    /**
     * Generate JWT token from UserDetails
     * ✅ Comprehensive error handling and validation
     */
    public String generateTokenFromUsername(UserDetails userDetails) {
        try {
            // Validate input
            if (userDetails == null) {
                throw new IllegalArgumentException("UserDetails cannot be null");
            }

            // Validate JWT secret configuration
            if (jwtSecret == null || jwtSecret.isEmpty()) {
                throw new IllegalStateException("JWT secret not configured. Set spring.app.jwtSecret in application.properties");
            }

            // Cast to UserEntity to get additional claims
            if (!(userDetails instanceof UserEntity user)) {
                log.warn("⚠️ UserDetails is not UserEntity, using minimal claims");
                throw new IllegalStateException("Invalid user details type");
            }

            // Build claims
            Claims claims = Jwts.claims().setSubject(user.getUsername());
            claims.put("fullName", user.getFullName() != null ? user.getFullName() : "");
            claims.put("email", user.getEmail());
            claims.put("bankId", user.getBank() != null ? user.getBank().getId() : null);
            claims.put("vendorId", user.getVendor() != null ? user.getVendor().getId() : null);

            // Extract and add roles
            List<String> roles = user.getAuthorities().stream()
                    .map(a -> a.getAuthority())
                    .collect(Collectors.toList());
            claims.put("roles", roles);

            // Set timestamps
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

            // Build and sign token
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();

            log.info("✅ JWT token generated for user: {} | Expires in: {} ms", user.getEmail(), jwtExpirationMs);
            return token;

        } catch (IllegalArgumentException ex) {
            log.error("❌ Invalid argument when generating token: {}", ex.getMessage());
            throw new RuntimeException("Invalid token generation parameters", ex);
        } catch (IllegalStateException ex) {
            log.error("❌ Configuration error: {}", ex.getMessage());
            throw new RuntimeException("JWT configuration error", ex);
        } catch (Exception ex) {
            log.error("❌ Unexpected error generating JWT token: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to generate JWT token", ex);
        }
    }

    /**
     * Get username from token with error handling
     * ✅ Safe extraction with proper error handling
     */
    public String getUserNameFromJwtToken(String token) {
        try {
            if (token == null || token.isEmpty()) {
                log.warn("⚠️ Token is null or empty");
                return null;
            }

            Claims claims = parseClaims(token);
            String username = claims.getSubject();

            if (username == null || username.isEmpty()) {
                log.warn("⚠️ Username claim is empty in token");
                return null;
            }

            log.debug("✅ Username extracted from token: {}", username);
            return username;

        } catch (ExpiredJwtException ex) {
            log.warn("⚠️ Token expired: {}", ex.getMessage());
            return null;
        } catch (JwtException ex) {
            log.warn("⚠️ Invalid JWT token: {}", ex.getMessage());
            return null;
        } catch (Exception ex) {
            log.error("❌ Unexpected error extracting username from token: {}", ex.getMessage());
            return null;
        }
    }

    /**
     * Get email from token
     * ✅ Safe extraction
     */
    public String getEmailFromToken(String token) {
        try {
            if (token == null || token.isEmpty()) {
                return null;
            }

            Claims claims = parseClaims(token);
            return claims.get("email", String.class);

        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("⚠️ Could not extract email from token: {}", ex.getMessage());
            return null;
        }
    }

    /**
     * Parse and validate JWT claims
     * ✅ Comprehensive error handling
     */
    public Claims parseClaims(String token) {
        try {
            if (token == null || token.isEmpty()) {
                throw new IllegalArgumentException("Token cannot be null or empty");
            }

            // Validate token structure (3 parts separated by .)
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new MalformedJwtException("Invalid JWT structure");
            }

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            log.debug("✅ JWT claims parsed successfully");
            return claims;

        } catch (MalformedJwtException ex) {
            log.warn("⚠️ Malformed JWT token: {}", ex.getMessage());
            throw ex;
        } catch (ExpiredJwtException ex) {
            log.warn("⚠️ JWT token expired: {}", ex.getMessage());
            throw ex;
        } catch (UnsupportedJwtException ex) {
            log.warn("⚠️ Unsupported JWT token: {}", ex.getMessage());
            throw ex;
        } catch (SignatureException ex) {
            log.warn("⚠️ Invalid JWT signature: {}", ex.getMessage());
            throw ex;
        } catch (IllegalArgumentException ex) {
            log.warn("⚠️ JWT claims string is empty: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("❌ Unexpected error parsing JWT: {}", ex.getMessage());
            throw new JwtException("Failed to parse JWT", ex);
        }
    }

    /**
     * Validate JWT token
     * ✅ Comprehensive validation with proper error classification
     */
    public boolean validateJwtToken(String authToken) {
        try {
            if (authToken == null || authToken.isEmpty()) {
                log.debug("⚠️ Token is null or empty");
                return false;
            }

            // Verify signing key is configured
            if (jwtSecret == null || jwtSecret.isEmpty()) {
                log.error("❌ JWT secret not configured");
                return false;
            }

            parseClaims(authToken);
            log.debug("✅ JWT token validation successful");
            return true;

        } catch (ExpiredJwtException ex) {
            log.warn("⚠️ JWT token has expired: {}", ex.getMessage());
            return false;
        } catch (UnsupportedJwtException ex) {
            log.warn("⚠️ JWT token is unsupported: {}", ex.getMessage());
            return false;
        } catch (MalformedJwtException ex) {
            log.warn("⚠️ Malformed JWT token: {}", ex.getMessage());
            return false;
        } catch (SignatureException ex) {
            log.warn("⚠️ JWT signature validation failed: {}", ex.getMessage());
            return false;
        } catch (IllegalArgumentException ex) {
            log.warn("⚠️ JWT claims string is empty: {}", ex.getMessage());
            return false;
        } catch (JwtException ex) {
            log.warn("⚠️ JWT validation failed: {}", ex.getMessage());
            return false;
        } catch (Exception ex) {
            log.error("❌ Unexpected error validating JWT: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * Check if token is expired
     * ✅ Safe expiry check
     */
    public boolean isTokenExpired(String token) {
        try {
            if (token == null || token.isEmpty()) {
                return true;
            }

            Claims claims = parseClaims(token);
            Date expiration = claims.getExpiration();

            if (expiration == null) {
                log.warn("⚠️ Token has no expiration date");
                return false;
            }

            boolean isExpired = expiration.before(new Date());
            if (isExpired) {
                log.info("ℹ️ Token expired at: {}", expiration);
            }
            return isExpired;

        } catch (ExpiredJwtException ex) {
            log.info("ℹ️ Token is expired");
            return true;
        } catch (Exception ex) {
            log.error("❌ Error checking token expiration: {}", ex.getMessage());
            return true; // Assume expired on error
        }
    }

    /**
     * Get signing key for JWT operations
     * ✅ Proper key derivation with error handling
     */
    private Key getSigningKey() {
        try {
            if (jwtSecret == null || jwtSecret.isEmpty()) {
                throw new IllegalStateException("JWT secret is not configured");
            }

            byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);

            if (keyBytes.length < 32) {
                log.warn("⚠️ JWT secret is too short. Minimum 32 bytes (256 bits) recommended for HS256");
            }

            return Keys.hmacShaKeyFor(keyBytes);

        } catch (IllegalArgumentException ex) {
            log.error("❌ Invalid Base64 JWT secret: {}", ex.getMessage());
            throw new RuntimeException("JWT secret must be valid Base64 encoded", ex);
        } catch (IllegalStateException ex) {
            log.error("❌ Configuration error: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("❌ Error creating signing key: {}", ex.getMessage());
            throw new RuntimeException("Failed to create JWT signing key", ex);
        }
    }
}