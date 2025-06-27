package com.dev.MedicalAppointmentSystemAPI.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles unauthorized access attempts by returning a JSON error response.
 * Used as the authentication entry point for invalid or missing JWT tokens.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    private final ObjectMapper objectMapper;

    /**
     * Constructs JwtAuthenticationEntryPoint with an ObjectMapper.
     * Configures ObjectMapper to handle Java 8 date/time types.
     * @param objectMapper The ObjectMapper for JSON serialization.
     */
    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.objectMapper.registerModule(new JavaTimeModule()); // Support for LocalDateTime
    }

    /**
     * Responds with an HTTP 401 Unauthorized error when authentication fails.
     * Returns a JSON error response with details about the unauthorized access.
     * @param request The HTTP request.
     * @param response The HTTP response.
     * @param authException The authentication exception.
     * @throws IOException If an I/O error occurs while writing the response.
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        logger.error("Unauthorized access: {} {} - {}", request.getMethod(), request.getRequestURI(), authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        errorDetails.put("error", "Unauthorized");
        errorDetails.put("message", authException.getMessage() != null ? authException.getMessage() : "Invalid or missing authentication credentials");
        errorDetails.put("path", request.getRequestURI());
        errorDetails.put("timestamp", LocalDateTime.now());

        try {
            objectMapper.writeValue(response.getWriter(), errorDetails);
        } catch (IOException e) {
            logger.error("Failed to write JSON error response: {}", e.getMessage());
            throw e; // Rethrow to ensure proper error handling
        }
    }
}