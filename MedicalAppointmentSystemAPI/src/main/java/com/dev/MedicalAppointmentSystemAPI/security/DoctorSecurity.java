package com.dev.MedicalAppointmentSystemAPI.security;

import com.dev.MedicalAppointmentSystemAPI.model.Doctor;
import com.dev.MedicalAppointmentSystemAPI.model.User;
import com.dev.MedicalAppointmentSystemAPI.repository.DoctorRepository;
import com.dev.MedicalAppointmentSystemAPI.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Security component for enforcing access control on doctor-related operations.
 * Used in @PreAuthorize expressions to verify ownership.
 */
@Component("doctorSecurity")
public class DoctorSecurity {

    private static final Logger logger = LoggerFactory.getLogger(DoctorSecurity.class);

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    /**
     * Constructs DoctorSecurity with required repositories.
     * @param doctorRepository Repository for doctor operations.
     * @param userRepository Repository for user operations.
     */
    public DoctorSecurity(DoctorRepository doctorRepository, UserRepository userRepository) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
    }

    /**
     * Checks if the authenticated user is associated with the specified doctor.
     * Requires the user to have ROLE_DOCTOR.
     * @param doctorId The ID of the doctor (non-null).
     * @param userDetails The authenticated user's details.
     * @return true if the user is the doctor, false otherwise.
     * @throws IllegalArgumentException if doctorId is null or doctor/user not found.
     */
    public boolean isDoctorOwner(@NotNull(message = "Doctor ID cannot be null") Long doctorId,
                                 UserDetails userDetails) {
        logger.debug("Checking if user {} is owner of doctor ID {}", userDetails.getUsername(), doctorId);

        if (!userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"))) {
            logger.warn("User {} lacks ROLE_DOCTOR for doctor ID {}", userDetails.getUsername(), doctorId);
            return false;
        }

        User user = userRepository.findByUsernameIgnoreCase(userDetails.getUsername())
                .orElseThrow(() -> {
                    logger.error("User not found with username: {}", userDetails.getUsername());
                    return new IllegalArgumentException("User not found with username: " + userDetails.getUsername());
                });

        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> {
                    logger.error("Doctor not found for user ID: {}", user.getId());
                    return new IllegalArgumentException("Doctor not found for user ID: " + user.getId());
                });

        boolean isOwner = Objects.equals(doctor.getId(), doctorId);
        logger.info("User {} {} doctor ID {}", userDetails.getUsername(), isOwner ? "is" : "is not", doctorId);
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