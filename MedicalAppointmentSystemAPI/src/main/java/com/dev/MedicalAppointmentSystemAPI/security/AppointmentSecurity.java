package com.dev.MedicalAppointmentSystemAPI.security;

import com.dev.MedicalAppointmentSystemAPI.model.Appointment;
import com.dev.MedicalAppointmentSystemAPI.model.Doctor;
import com.dev.MedicalAppointmentSystemAPI.model.Patient;
import com.dev.MedicalAppointmentSystemAPI.model.User;
import com.dev.MedicalAppointmentSystemAPI.repository.AppointmentRepository;
import com.dev.MedicalAppointmentSystemAPI.repository.DoctorRepository;
import com.dev.MedicalAppointmentSystemAPI.repository.PatientRepository;
import com.dev.MedicalAppointmentSystemAPI.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Security component for enforcing access control on appointments.
 * Used in @PreAuthorize expressions to verify ownership and assignment relationships.
 */
@Component("appointmentSecurity")
public class AppointmentSecurity {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentSecurity.class);

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    /**
     * Constructs AppointmentSecurity with required repositories.
     * @param appointmentRepository Repository for appointment operations.
     * @param patientRepository Repository for patient operations.
     * @param doctorRepository Repository for doctor operations.
     * @param userRepository Repository for user operations.
     */
    public AppointmentSecurity(AppointmentRepository appointmentRepository,
                               PatientRepository patientRepository,
                               DoctorRepository doctorRepository,
                               UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
    }

    /**
     * Checks if the authenticated user is the patient associated with the appointment.
     * Requires the user to have ROLE_PATIENT.
     * @param appointmentId The ID of the appointment (non-null).
     * @param userDetails The authenticated user's details.
     * @return true if the user owns the appointment, false otherwise.
     * @throws IllegalArgumentException if appointmentId is null or appointment/user not found.
     */
    public boolean isAppointmentOwner(@NotNull(message = "Appointment ID cannot be null") Long appointmentId,
                                      UserDetails userDetails) {
        logger.debug("Checking if user {} owns appointment ID {}", userDetails.getUsername(), appointmentId);

        if (!userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT"))) {
            logger.warn("User {} lacks ROLE_PATIENT for appointment ID {}", userDetails.getUsername(), appointmentId);
            return false;
        }

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> {
                    logger.error("Appointment not found with ID: {}", appointmentId);
                    return new IllegalArgumentException("Appointment not found with ID: " + appointmentId);
                });

        Patient patient = appointment.getPatient();
        if (patient == null || patient.getUser() == null) {
            logger.error("No patient or user associated with appointment ID: {}", appointmentId);
            throw new IllegalArgumentException("No patient or user associated with appointment ID: " + appointmentId);
        }

        boolean isOwner = Objects.equals(patient.getUser().getUsername(), userDetails.getUsername());
        logger.info("User {} {} appointment ID {}", userDetails.getUsername(), isOwner ? "owns" : "does not own", appointmentId);
        return isOwner;
    }

    /**
     * Checks if the authenticated doctor has an appointment with the specified patient.
     * Requires the user to have ROLE_DOCTOR.
     * @param patientId The ID of the patient (non-null).
     * @param userDetails The authenticated user's details.
     * @return true if the doctor is assigned to the patient, false otherwise.
     * @throws IllegalArgumentException if patientId is null or user/doctor/patient not found.
     */
    public boolean isPatientAssignedToDoctor(@NotNull(message = "Patient ID cannot be null") Long patientId,
                                             UserDetails userDetails) {
        logger.debug("Checking if user {} (doctor) is assigned to patient ID {}", userDetails.getUsername(), patientId);

        if (!userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"))) {
            logger.warn("User {} lacks ROLE_DOCTOR for patient ID {}", userDetails.getUsername(), patientId);
            return false;
        }

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> {
                    logger.error("Patient not found with ID: {}", patientId);
                    return new IllegalArgumentException("Patient not found with ID: " + patientId);
                });

        User user = userRepository.findByUsernameIgnoreCase(userDetails.getUsername())
                .orElseThrow(() -> {
                    logger.error("User not found with username: {}", userDetails.getUsername());
                    return new IllegalArgumentException("User not found with username: " + userDetails.getUsername());
                });

        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> {
                    logger.error("Doctor not found for user ID: {}", user.getId());
                    return new IllegalArgumentException("Doctor not found for user ID: " + user.getId());
                });

        boolean isAssigned = appointmentRepository.existsByPatientIdAndDoctorId(patient.getId(), doctor.getId());
        logger.info("Doctor {} {} assigned to patient ID {}", userDetails.getUsername(), isAssigned ? "is" : "is not", patientId);
        return isAssigned;
    }

    /**
     * Checks if the authenticated patient has an appointment with the specified doctor.
     * Requires the user to have ROLE_PATIENT.
     * @param doctorId The ID of the doctor (non-null).
     * @param userDetails The authenticated user's details.
     * @return true if the patient is assigned to the doctor, false otherwise.
     * @throws IllegalArgumentException if doctorId is null or user/doctor/patient not found.
     */
    public boolean isDoctorAssignedToPatient(@NotNull(message = "Doctor ID cannot be null") Long doctorId,
                                             UserDetails userDetails) {
        logger.debug("Checking if user {} (patient) is assigned to doctor ID {}", userDetails.getUsername(), doctorId);

        if (!userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT"))) {
            logger.warn("User {} lacks ROLE_PATIENT for doctor ID {}", userDetails.getUsername(), doctorId);
            return false;
        }

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> {
                    logger.error("Doctor not found with ID: {}", doctorId);
                    return new IllegalArgumentException("Doctor not found with ID: " + doctorId);
                });

        User user = userRepository.findByUsernameIgnoreCase(userDetails.getUsername())
                .orElseThrow(() -> {
                    logger.error("User not found with username: {}", userDetails.getUsername());
                    return new IllegalArgumentException("User not found with username: " + userDetails.getUsername());
                });

        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> {
                    logger.error("Patient not found for user ID: {}", user.getId());
                    return new IllegalArgumentException("Patient not found for user ID: " + user.getId());
                });

        boolean isAssigned = appointmentRepository.existsByPatientIdAndDoctorId(patient.getId(), doctor.getId());
        logger.info("Patient {} {} assigned to doctor ID {}", userDetails.getUsername(), isAssigned ? "is" : "is not", doctorId);
        return isAssigned;
    }
}