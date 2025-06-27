package com.dev.MedicalAppointmentSystemAPI.exception;

import java.io.Serial;

/**
 * Custom exception thrown when a requested resource (e.g., Appointment, Patient, Doctor) is not found in the system.
 * Used to trigger an HTTP 404 response with detailed error information.
 */
public class ResourceNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String resourceType;
    private final String resourceId;

    /**
     * Constructs a new ResourceNotFoundException with the specified message.
     * @param message The detail message (e.g., "Appointment not found with ID: 1").
     */
    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceType = null;
        this.resourceId = null;
    }

    /**
     * Constructs a new ResourceNotFoundException with the specified message and cause.
     * @param message The detail message.
     * @param cause The cause of the exception (can be null).
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.resourceType = null;
        this.resourceId = null;
    }

    /**
     * Constructs a new ResourceNotFoundException with structured resource information.
     * @param resourceType The type of resource (e.g., "Appointment", "Patient").
     * @param resourceId The identifier of the resource (e.g., "1").
     */
    public ResourceNotFoundException(String resourceType, String resourceId) {
        super(String.format("%s not found with ID: %s", resourceType, resourceId));
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    /**
     * Constructs a new ResourceNotFoundException with structured resource information and cause.
     * @param resourceType The type of resource.
     * @param resourceId The identifier of the resource.
     * @param cause The cause of the exception (can be null).
     */
    public ResourceNotFoundException(String resourceType, String resourceId, Throwable cause) {
        super(String.format("%s not found with ID: %s", resourceType, resourceId), cause);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    /**
     * Returns the type of resource that was not found.
     * @return The resource type (e.g., "Appointment"), or null if not specified.
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * Returns the identifier of the resource that was not found.
     * @return The resource ID (e.g., "1"), or null if not specified.
     */
    public String getResourceId() {
        return resourceId;
    }
}