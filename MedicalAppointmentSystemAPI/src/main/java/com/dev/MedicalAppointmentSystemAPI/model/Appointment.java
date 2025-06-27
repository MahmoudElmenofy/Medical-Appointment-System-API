package com.dev.MedicalAppointmentSystemAPI.model;

import jakarta.persistence.*;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDateTime;

/**
 * Entity representing an appointment in the medical appointment system.
 */
@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"patient", "doctor"})
@ToString(exclude = {"patient", "doctor"})
@Appointment.ValidEndDateTime
public class Appointment {

	/**
	 * Unique identifier for the appointment.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Patient associated with the appointment.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "patient_id", nullable = false)
	@NotNull(message = "Patient cannot be null")
	private Patient patient;

	/**
	 * Doctor associated with the appointment.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "doctor_id", nullable = false)
	@NotNull(message = "Doctor cannot be null")
	private Doctor doctor;

	/**
	 * Date and time of the appointment (must be in the future).
	 */
	@NotNull(message = "Appointment date cannot be null")
	@Future(message = "Appointment date must be in the future")
	@Column(name = "appointment_datetime", nullable = false)
	private LocalDateTime appointmentDateTime;

	/**
	 * End date and time of the appointment (optional, must be after appointmentDateTime if present).
	 */
	@Column(name = "end_datetime")
	private LocalDateTime endDateTime;

	/**
	 * Status of the appointment (e.g., SCHEDULED, CONFIRMED, CANCELLED, COMPLETED).
	 */
	@NotNull(message = "Status cannot be null")
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AppointmentStatus status;

	/**
	 * Reason for the appointment (max 500 characters).
	 */
	@Size(max = 500, message = "Reason cannot exceed 500 characters")
	private String reason;

	/**
	 * Additional notes for the appointment (max 1000 characters).
	 */
	@Size(max = 1000, message = "Notes cannot exceed 1000 characters")
	private String notes;

	/**
	 * Timestamp when the appointment was created.
	 */
	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	/**
	 * Timestamp when the appointment was last updated.
	 */
	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	// Custom constraint for endDateTime validation
	@Constraint(validatedBy = EndDateTimeValidator.class)
	@Target({ElementType.TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ValidEndDateTime {
		String message() default "End date must be after appointment date";
		Class<?>[] groups() default {};
		Class<? extends Payload>[] payload() default {};
	}

	// Validator for endDateTime
	public static class EndDateTimeValidator implements jakarta.validation.ConstraintValidator<ValidEndDateTime, Appointment> {
		@Override
		public void initialize(ValidEndDateTime constraintAnnotation) {
		}

		@Override
		public boolean isValid(Appointment appointment, jakarta.validation.ConstraintValidatorContext context) {
			if (appointment.getEndDateTime() == null) {
				return true; // Null endDateTime is valid
			}
			return appointment.getEndDateTime().isAfter(appointment.getAppointmentDateTime());
		}
	}

    /*
    // Explicit getters (uncomment if Lombok issues persist)
    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public LocalDateTime getAppointmentDateTime() {
        return appointmentDateTime;
    }
    */
}