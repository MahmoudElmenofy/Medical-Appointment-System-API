package com.dev.MedicalAppointmentSystemAPI.repository;

import com.dev.MedicalAppointmentSystemAPI.model.ERole;
import com.dev.MedicalAppointmentSystemAPI.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for accessing Role entities in the database.
 * Provides CRUD operations and custom query methods for roles.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Finds a role by its name (ERole enum value).
     * @param name The ERole enum value to search for (e.g., ADMIN, DOCTOR, PATIENT).
     * @return An Optional containing the role, or empty if not found.
     */
    Optional<com.dev.MedicalAppointmentSystemAPI.model.Role> findByName(ERole name);

    /**
     * Checks if a role exists with the specified name.
     * @param name The ERole enum value to check.
     * @return true if a role with the name exists, false otherwise.
     */
    boolean existsByName(ERole name);
}