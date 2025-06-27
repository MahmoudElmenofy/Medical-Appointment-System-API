package com.dev.MedicalAppointmentSystemAPI.repository;

import com.dev.MedicalAppointmentSystemAPI.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for accessing Patient entities in the database.
 * Provides CRUD operations and custom query methods for patients.
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Finds a patient by their email address.
     * @param email The email address to search for.
     * @return An Optional containing the patient, or empty if not found.
     */
    Optional<Patient> findByEmail(String email);

    /**
     * Finds a patient by their email address, ignoring case.
     * @param email The email address to search for.
     * @return An Optional containing the patient, or empty if not found.
     */
    Optional<Patient> findByEmailIgnoreCase(String email);

    /**
     * Checks if a patient exists with the specified email address.
     * @param email The email address to check.
     * @return true if a patient with the email exists, false otherwise.
     */
    boolean existsByEmail(String email);

    /**
     * Finds a patient by their associated user ID.
     * @param userId The ID of the associated user.
     * @return An Optional containing the patient, or empty if not found.
     */
    Optional<Patient> findByUserId(Long userId);
    /**
     * Finds a patient by their ID.
     * @param id The ID of the patient.
     * @return An Optional containing the patient, or empty if not found.
     */
    Optional<Patient> findById(Long id);


}