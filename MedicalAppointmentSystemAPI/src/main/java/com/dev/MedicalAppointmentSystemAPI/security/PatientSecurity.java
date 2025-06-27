package com.dev.MedicalAppointmentSystemAPI.security;

import com.dev.MedicalAppointmentSystemAPI.model.Patient;
import com.dev.MedicalAppointmentSystemAPI.model.User;
import com.dev.MedicalAppointmentSystemAPI.repository.PatientRepository;
import com.dev.MedicalAppointmentSystemAPI.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Security component for enforcing access control on patient-related operations.
 * Used in @PreAuthorize expressions to verify ownership.
 */
@Component("patientSecurity")
public class PatientSecurity {

    private static final Logger logger = LoggerFactory.getLogger(PatientSecurity.class);

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    /**
     * Constructs PatientSecurity with required repositories.
     * @param patientRepository Repository for patient operations.
     * @param userRepository Repository for user operations.
     */
    public PatientSecurity(PatientRepository patientRepository, UserRepository userRepository) {
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    /**
     * Checks if the authenticated user is associated with the specified patient.
     * Requires the user to have ROLE_PATIENT.
     * @param patientId The ID of the patient (non-null).
     * @param userDetails The authenticated user's details.
     * @return true if the user is the patient, false otherwise.
     * @throws IllegalArgumentException if patientId is null or patient/user not found.
     */
    public boolean isPatientOwner(@NotNull(message = "Patient ID cannot be null") Long patientId,
                                  UserDetails userDetails) {
        logger.debug("Checking if user {} is owner of patient ID {}", userDetails.getUsername(), patientId);

        if (!userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT"))) {
            logger.warn("User {} lacks ROLE_PATIENT for patient ID {}", userDetails.getUsername(), patientId);
            return false;
        }

        User user = userRepository.findByUsernameIgnoreCase(userDetails.getUsername())
                .orElseThrow(() -> {
                    logger.error("User not found with username: {}", userDetails.getUsername());
                    return new IllegalArgumentException("User not found with username: " + userDetails.getUsername());
                });

        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> {
                    logger.error("Patient not found for user ID: {}", user.getId());
                    return new IllegalArgumentException("Patient not found for user ID: " + user.getId());
                });

        boolean isOwner = Objects.equals(patient.getId(), patientId);
        logger.info("User {} {} patient ID {}", userDetails.getUsername(), isOwner ? "is" : "is not", patientId);
        return isOwner;
    }

    /**
     * Checks if the authenticated user matches the specified user ID.
     * @param userId The ID of the user (non-null).
     * @param userDetails The authenticated user's details.
     * @return true if the user matches the ID, false otherwise.
     * @throws IllegalArgumentException if userId is null or user not found.
     */
    public boolean isUserOwner(@NotNull(message = "User ID cannot be null") Long userId,
                               UserDetails userDetails) {
        logger.debug("Checking if user {} is owner of user ID {}", userDetails.getUsername(), userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", userId);
                    return new IllegalArgumentException("User not found with ID: " + userId);
                });

        boolean isOwner = Objects.equals(user.getUsername(), userDetails.getUsername());
        logger.info("User {} {} user ID {}", userDetails.getUsername(), isOwner ? "is" : "is not", userId);
        return isOwner;
    }
}