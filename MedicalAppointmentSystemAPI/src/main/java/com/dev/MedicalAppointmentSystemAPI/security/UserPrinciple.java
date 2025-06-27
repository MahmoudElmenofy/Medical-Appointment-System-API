package com.dev.MedicalAppointmentSystemAPI.security;

import com.dev.MedicalAppointmentSystemAPI.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementation of UserDetails to provide user information for Spring Security authentication.
 * Maps User entity fields and roles to UserDetails properties.
 */
public class UserPrinciple implements UserDetails {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(UserPrinciple.class);

    private final Long id;
    private final String username;
    private final String email;

    @JsonIgnore
    private final String password;

    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * Constructs a UserPrinciple with user details and authorities.
     * @param id The user ID (non-null).
     * @param username The username (non-null).
     * @param email The user email (non-null).
     * @param password The user password (non-null).
     * @param authorities The user authorities (non-null).
     * @throws IllegalArgumentException if any parameter is null.
     */
    public UserPrinciple(@NotNull(message = "ID cannot be null") Long id,
                         @NotNull(message = "Username cannot be null") String username,
                         @NotNull(message = "Email cannot be null") String email,
                         @NotNull(message = "Password cannot be null") String password,
                         @NotNull(message = "Authorities cannot be null") Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        logger.debug("Created UserPrinciple for username: {}", username);
    }

    /**
     * Creates a UserPrinciple from a User entity.
     * Maps user roles to authorities with ROLE_ prefix.
     * @param user The User entity (non-null).
     * @return A configured UserPrinciple.
     * @throws IllegalArgumentException if user or required fields are null.
     */
    public static UserPrinciple create(@NotNull(message = "User cannot be null") User user) {
        logger.debug("Creating UserPrinciple for user: {}", user.getUsername());

        if (user.getId() == null || user.getUsername() == null || user.getEmail() == null || user.getPassword() == null || user.getRoles() == null) {
            logger.error("User has null fields: id={}, username={}, email={}, password={}, roles={}",
                    user.getId(), user.getUsername(), user.getEmail(), user.getPassword() != null ? "[REDACTED]" : null, user.getRoles());
            throw new IllegalArgumentException("User fields cannot be null");
        }

        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> {
                    String authority = "ROLE_" + role.getName().name();
                    logger.trace("Mapping role {} to authority {}", role.getName().name(), authority);
                    return new SimpleGrantedAuthority(authority);
                })
                .collect(Collectors.toList());

        return new UserPrinciple(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    /**
     * Returns the user ID.
     * @return The user ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the user email.
     * @return The user email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the username for authentication.
     * @return The username.
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Returns the password for authentication.
     * @return The password.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Returns the authorities granted to the user.
     * @return The authorities.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Indicates whether the user's account has not expired.
     * @return true (account never expires).
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user's account is not locked.
     * @return true (account never locked).
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials have not expired.
     * @return true (credentials never expire).
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled.
     * @return true (user always enabled).
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Checks equality based on user ID.
     * @param o The object to compare.
     * @return true if IDs are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPrinciple that = (UserPrinciple) o;
        return Objects.equals(id, that.id);
    }

    /**
     * Generates hash code based on user ID.
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}