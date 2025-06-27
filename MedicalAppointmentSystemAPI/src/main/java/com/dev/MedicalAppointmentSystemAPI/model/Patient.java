package com.dev.MedicalAppointmentSystemAPI.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a patient in the Medical Appointment System.
 */
@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"appointments", "user"})
@ToString(exclude = {"appointments", "user"})
public class Patient {

    /**
     * Unique identifier for the patient.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * First name of the patient (required, max 50 characters).
     */
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /**
     * Last name of the patient (required, max 50 characters).
     */
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    /**
     * Email address of the patient (required, unique, valid email format, max 100 characters).
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Phone number of the patient (optional, max 20 characters, valid format).
     */
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    @Pattern(regexp = "^(\\+?[1-9]\\d{1,14})?$", message = "Phone number must be a valid international format")
    @Column(name = "phone_number")
    private String phoneNumber;

    /**
     * Date of birth of the patient (optional).
     */
    @Column(name = "date_of_birth")
    @PastOrPresent(message = "Date of birth cannot be in the future")
    private LocalDate dateOfBirth;

    /**
     * Gender of the patient (optional, max 10 characters).
     */
    @Size(max = 10, message = "Gender cannot exceed 10 characters")
    private String gender;

    /**
     * Address of the patient (optional, max 200 characters).
     */
    @Size(max = 200, message = "Address cannot exceed 200 characters")
    private String address;

    /**
     * Medical history of the patient (optional, max 500 characters).
     */
    @Size(max = 500, message = "Medical history cannot exceed 500 characters")
    @Column(name = "medical_history")
    private String medicalHistory;

    /**
     * Associated user account (optional).
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    /**
     * List of appointments associated with the patient.
     */
    @Builder.Default
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appointment> appointments = new ArrayList<>();

    /**
     * Timestamp when the patient record was created.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the patient record was last updated.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}