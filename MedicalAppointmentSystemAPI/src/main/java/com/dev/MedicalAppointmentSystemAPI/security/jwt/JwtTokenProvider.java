package com.dev.MedicalAppointmentSystemAPI.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

/**
 * Utility class for generating, validating, and parsing JSON Web Tokens (JWTs).
 * Used for securing API endpoints with token-based authentication.
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final String jwtSecret;
    private final int jwtExpirationInMs;

    /**
     * Constructs JwtTokenProvider with JWT configuration properties.
     * @param jwtSecret The Base64-encoded JWT secret key.
     * @param jwtExpirationInMs The JWT expiration time in milliseconds.
     */
    public JwtTokenProvider(@Value("${jwt.secret}") String jwtSecret,
                            @Value("${jwt.expiration}") int jwtExpirationInMs) {
        this.jwtSecret = jwtSecret;
        this.jwtExpirationInMs = jwtExpirationInMs;

        // Warn if secret is too short (256 bits = ~44 Base64 chars)
        if (jwtSecret.length() < 44) {
            logger.warn("JWT secret is shorter than recommended 256 bits. Current length: {} chars. Consider using a stronger key in production.", jwtSecret.length());
        }
        logger.info("JwtTokenProvider initialized with expiration: {} ms", jwtExpirationInMs);
    }

    /**
     * Generates a signing key from the Base64-encoded JWT secret.
     * @return The HMAC-SHA key for signing JWTs.
     */
    private Key getSigningKey() {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
            if (keyBytes.length < 32) {
                logger.warn("Decoded JWT secret is shorter than 256 bits ({} bytes). Using provided key for development.", keyBytes.length);
            }
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid Base64-encoded JWT secret: {}", e.getMessage());
            throw new IllegalStateException("Invalid JWT secret configuration", e);
        }
    }

    /**
     * Generates a JWT for the authenticated user.
     * Includes username as the subject.
     * @param authentication The authentication object.
     * @return The generated JWT.
     */
    public String generateToken(Authentication authentication) {
        logger.debug("Generating JWT for principal: {}", authentication.getName());

        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts the username from a JWT.
     * @param token The JWT.
     * @return The username.
     */
    public String getUsernameFromJWT(String token) {
        logger.debug("Extracting username from JWT");
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String username = claims.getSubject();
            logger.trace("Extracted username: {}", username);
            return username;
        } catch (Exception e) {
            logger.error("Failed to extract username from JWT: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Validates a JWT.
     * @param token The JWT.
     * @return True if valid, false otherwise.
     */
    public boolean validateToken(String token) {
        logger.debug("Validating JWT");
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            logger.trace("JWT validated successfully");
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Invalid JWT: {}", e.getMessage());
            return false;
        }
    }
}