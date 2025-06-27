package com.dev.MedicalAppointmentSystemAPI.security.jwt;

import org.springframework.security.core.AuthenticationException;

/**
 * Custom exception for JWT authentication failures.
 */
public class JwtAuthenticationException extends AuthenticationException {

    /**
     * Constructs a JwtAuthenticationException with the specified message.
     * @param message The error message.
     */
    public JwtAuthenticationException(String message) {
        super(message);
    }

    /**
     * Constructs a JwtAuthenticationException with the specified message and cause.
     * @param message The error message.
     * @param cause The cause of the exception.
     */
    public JwtAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}