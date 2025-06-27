package com.dev.MedicalAppointmentSystemAPI.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a doctor in the Medical Appointment System.
 */
@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"appointments", "user"})
@ToString(exclude = {"appointments", "user"})
public class Doctor {

    /**
     * Unique identifier for the doctor.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * First name of the doctor (required, max 50 characters).
     */
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /**
     * Last name of the doctor (required, max 50 characters).
     */
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    /**
     * Email address of the doctor (required, unique, valid email format, max 100 characters).
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Phone number of the doctor (optional, max 20 characters, valid format).
     */
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    @Pattern(regexp = "^(\\+?[1-9]\\d{1,14})?$", message = "Phone number must be a valid international format")
    @Column(name = "phone_number")
    private String phoneNumber;

    /**
     * Specialization of the doctor (optional, max 100 characters).
     */
    @Size(max = 100, message = "Specialization cannot exceed 100 characters")
    @Column(name = "specialization")
    private String specialization;

    /**
     * Qualifications of the doctor (optional, max 500 characters).
     */
    @Size(max = 500, message = "Qualifications cannot exceed 500 characters")
    @Column(name = "qualifications")
    private String qualifications;

    /**
     * Address of the doctor (optional, max 200 characters).
     */
    @Size(max = 200, message = "Address cannot exceed 200 characters")
    @Column(name = "address")
    private String address;

    /**
     * Start of the doctor's working hours (optional).
     */
    @Column(name = "working_hours_start")
    private LocalTime workingHoursStart;

    /**
     * End of the doctor's working hours (optional).
     */
    @Column(name = "working_hours_end")
    private LocalTime workingHoursEnd;

    /**
     * Associated user account (optional).
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    /**
     * List of appointments associated with the doctor.
     */
    @Builder.Default
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appointment> appointments = new ArrayList<>();

    /**
     * Timestamp when the doctor record was created.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the doctor record was last updated.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}