package com.dev.MedicalAppointmentSystemAPI.security;

import com.dev.MedicalAppointmentSystemAPI.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementation of UserDetailsService for loading user details during authentication.
 * Retrieves user data from UserRepository and maps to UserDetails via UserPrinciple.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final UserRepository userRepository;

    /**
     * Constructs UserDetailsServiceImpl with the required UserRepository.
     * @param userRepository Repository for user operations.
     */
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        logger.info("UserDetailsServiceImpl initialized with UserRepository");
    }

    /**
     * Loads user details by username for authentication.
     * Uses case-insensitive username matching.
     * @param username The username to search for.
     * @return UserDetails for the user.
     * @throws UsernameNotFoundException if the user is not found.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Loading user by username: {}", username);
        return userRepository.findByUsernameIgnoreCase(username)
                .map(user -> {
                    logger.info("User found with username: {}", username);
                    return UserPrinciple.create(user);
                })
                .orElseThrow(() -> {
                    logger.error("User not found with username: {}", username);
                    return new UsernameNotFoundException("User not found with username: " + username);
                });
    }

    /**
     * Loads user details by ID for authentication or authorization.
     * @param id The ID of the user.
     * @return UserDetails for the user.
     * @throws UsernameNotFoundException if the user is not found.
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) {
        logger.debug("Loading user by ID: {}", id);
        return userRepository.findById(id)
                .map(user -> {
                    logger.info("User found with ID: {}", id);
                    return UserPrinciple.create(user);
                })
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", id);
                    return new UsernameNotFoundException("User not found with ID: " + id);
                });
    }
}