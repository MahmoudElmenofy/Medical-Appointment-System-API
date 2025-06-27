package com.dev.MedicalAppointmentSystemAPI.controller;

import com.dev.MedicalAppointmentSystemAPI.exception.ResourceNotFoundException;
import com.dev.MedicalAppointmentSystemAPI.model.ERole;
import com.dev.MedicalAppointmentSystemAPI.model.Role;
import com.dev.MedicalAppointmentSystemAPI.model.User;
import com.dev.MedicalAppointmentSystemAPI.repository.RoleRepository;
import com.dev.MedicalAppointmentSystemAPI.repository.UserRepository;
import com.dev.MedicalAppointmentSystemAPI.security.UserPrinciple;
import com.dev.MedicalAppointmentSystemAPI.security.jwt.JwtTokenProvider;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST controller for managing user authentication and registration.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Constructs AuthController with required dependencies.
     * @param authenticationManager The authentication manager.
     * @param userRepository The user repository.
     * @param roleRepository The role repository.
     * @param encoder The password encoder.
     * @param jwtTokenProvider The JWT provider.
     */
    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder encoder,
                          JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtTokenProvider = jwtTokenProvider;
        logger.info("AuthController initialized");
    }

    /**
     * Authenticates a user and returns a JWT token.
     * @param loginRequest The login request with credentials.
     * @return Response with JWT token and user details.
     */
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.debug("Signin request for username: {}", loginRequest.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        String jwt = jwtTokenProvider.generateToken(authentication);

        UserPrinciple principal = (UserPrinciple) authentication.getPrincipal();
        logger.info("User authenticated successfully: {}", loginRequest.getUsername());
        return ResponseEntity.ok(new JwtResponse(
                jwt,
                principal.getUsername(),
                principal.getId(),
                principal.getAuthorities().stream().map(Object::toString).collect(Collectors.toList())
        ));
    }

    /**
     * Registers a new user with specified details.
     * @param signUpRequest The signup request with user details.
     * @return Response indicating success or failure.
     * @throws IllegalArgumentException If username or email is already in use.
     * @throws ResourceNotFoundException If requested roles are not found.
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        logger.debug("Signup request for username: {}", signUpRequest.getUsername());

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            logger.warn("Username already exists: {}", signUpRequest.getUsername());
            throw new IllegalArgumentException("Username is already taken");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            logger.warn("Email already exists: {}", signUpRequest.getEmail());
            throw new IllegalArgumentException("Email is already in use");
        }

        User user = new User(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword())
        );

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Role userRole = roleRepository.findByName(ERole.ROLE_PATIENT)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found: ROLE_PATIENT"));
            roles.add(userRole);
            logger.debug("Assigned default role: ROLE_PATIENT");
        } else {
            for (String roleStr : strRoles) {
                try {
                    ERole eRole = ERole.valueOf("ROLE_" + roleStr.toUpperCase());
                    Role role = roleRepository.findByName(eRole)
                            .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + eRole));
                    roles.add(role);
                    logger.debug("Assigned role: {}", eRole);
                } catch (IllegalArgumentException e) {
                    logger.warn("Invalid role requested: {}", roleStr);
                    throw new IllegalArgumentException("Invalid role: " + roleStr);
                }
            }
        }

        user.setRoles(roles);
        userRepository.save(user);

        logger.info("User registered successfully: {}", signUpRequest.getUsername());
        return ResponseEntity.ok(new MessageResponse("User registered successfully", "SUCCESS"));
    }

    /**
     * Request payload for user login.
     */
    @Setter
    @Getter
    public static class LoginRequest {
        @NotBlank(message = "Username cannot be empty")
        private String username;

        @NotBlank(message = "Password cannot be empty")
        private String password;

    }

    /**
     * Request payload for user registration.
     */
    @Setter
    @Getter
    public static class SignUpRequest {
        @NotBlank(message = "Username cannot be empty")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        private String username;

        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Invalid email format")
        @Size(max = 100, message = "Email cannot exceed 100 characters")
        private String email;

        @NotBlank(message = "Password cannot be empty")
        @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
        private String password;

        private Set<String> role;

    }

    /**
     * Response payload for successful authentication.
     */
    @Getter
    public static class JwtResponse {
        private String token;
        private String type = "Bearer";
        private Long userId;
        private String username;
        private List<String> roles;

        public JwtResponse(String token, String username, Long userId, List<String> roles) {
            this.token = token;
            this.username = username;
            this.userId = userId;
            this.roles = roles;
        }

    }

    /**
     * Response payload for API messages.
     */
    @Getter
    public static class MessageResponse {
        private String message;
        private String status;

        public MessageResponse(String message, String status) {
            this.message = message;
            this.status = status;
        }

    }
}