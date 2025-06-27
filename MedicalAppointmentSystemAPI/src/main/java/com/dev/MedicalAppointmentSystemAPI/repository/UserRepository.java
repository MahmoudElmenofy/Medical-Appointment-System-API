package com.dev.MedicalAppointmentSystemAPI.repository;

import com.dev.MedicalAppointmentSystemAPI.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for accessing User entities in the database.
 * Provides CRUD operations and custom query methods for users.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their username.
     * @param username The username to search for.
     * @return An Optional containing the user, or empty if not found.
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their username, ignoring case.
     * @param username The username to search for.
     * @return An Optional containing the user, or empty if not found.
     */
    Optional<User> findByUsernameIgnoreCase(String username);

    /**
     * Finds a user by their email address.
     * @param email The email address to search for.
     * @return An Optional containing the user, or empty if not found.
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by their email address, ignoring case.
     * @param email The email address to search for.
     * @return An Optional containing the user, or empty if not found.
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Checks if a user exists with the specified username.
     * @param username The username to check.
     * @return true if a user with the username exists, false otherwise.
     */
    Boolean existsByUsername(String username);

    /**
     * Checks if a user exists with the specified email address.
     * @param email The email address to check.
     * @return true if a user with the email exists, false otherwise.
     */
    Boolean existsByEmail(String email);
}