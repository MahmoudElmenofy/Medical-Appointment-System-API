package com.dev.MedicalAppointmentSystemAPI.repository;

import com.dev.MedicalAppointmentSystemAPI.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for accessing Doctor entities in the database.
 * Provides CRUD operations and custom query methods for doctors.
 */
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    /**
     * Finds a doctor by their email address.
     * @param email The email address to search for.
     * @return An Optional containing the doctor, or empty if not found.
     */
    Optional<Doctor> findByEmail(String email);

    /**
     * Finds a doctor by their email address, ignoring case.
     * @param email The email address to search for.
     * @return An Optional containing the doctor, or empty if not found.
     */
    Optional<Doctor> findByEmailIgnoreCase(String email);

    /**
     * Checks if a doctor exists with the specified email address.
     * @param email The email address to check.
     * @return true if a doctor with the email exists, false otherwise.
     */
    boolean existsByEmail(String email);

    /**
     * Finds a doctor by their associated user ID.
     * @param userId The ID of the associated user.
     * @return An Optional containing the doctor, or empty if not found.
     */
    Optional<Doctor> findByUserId(Long userId);

    /**
     * Checks if a doctor exists with the specified user ID.
     * @param userId The ID of the associated user.
     * @return true if a doctor with the user ID exists, false otherwise.
     */
    boolean existsByUserId(Long userId);

    /**
     * Finds doctors by their specialization.
     * @param specialization The specialization to search for.
     * @return A list of doctors with the specified specialization.
     */
    List<Doctor> findBySpecialization(String specialization);

    /**
     * Finds doctors by their specialization, ignoring case.
     * @param specialization The specialization to search for.
     * @return A list of doctors with the specified specialization.
     */
    List<Doctor> findBySpecializationIgnoreCase(String specialization);
}