package com.dev.MedicalAppointmentSystemAPI.service;

import com.dev.MedicalAppointmentSystemAPI.exception.ResourceNotFoundException;
import com.dev.MedicalAppointmentSystemAPI.model.Patient;
import com.dev.MedicalAppointmentSystemAPI.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing Patient entities.
 * Provides CRUD operations and business logic for patients in the Medical Appointment System.
 */
@Service
public class PatientService {

    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

    private final PatientRepository patientRepository;

    /**
     * Constructs a new PatientService with the specified PatientRepository.
     * @param patientRepository The repository for accessing patient data.
     */
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    /**
     * Retrieves all patients from the database.
     * @return A list of all patients.
     */
    @Transactional(readOnly = true)
    public List<Patient> getAllPatients() {
        logger.info("Fetching all patients");
        return patientRepository.findAll();
    }

    /**
     * Retrieves a patient by their ID.
     * @param id The ID of the patient.
     * @return An Optional containing the patient, or empty if not found.
     * @throws IllegalArgumentException if the ID is null.
     */
    @Transactional(readOnly = true)
    public Optional<Patient> getPatientById(Long id) {
        if (id == null) {
            logger.error("Patient ID cannot be null");
            throw new IllegalArgumentException("Patient ID cannot be null");
        }
        logger.info("Fetching patient with ID: {}", id);
        return patientRepository.findById(id);
    }

    /**
     * Retrieves a patient by their email address.
     * @param email The email address of the patient.
     * @return An Optional containing the patient, or empty if not found.
     * @throws IllegalArgumentException if the email is null or blank.
     */
    @Transactional(readOnly = true)
    public Optional<Patient> getPatientByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            logger.error("Email cannot be null or empty");
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        logger.info("Fetching patient with email: {}", email);
        return patientRepository.findByEmail(email);
    }

    /**
     * Retrieves a patient by their associated user ID.
     * @param userId The ID of the associated user.
     * @return An Optional containing the patient, or empty if not found.
     * @throws IllegalArgumentException if the user ID is null.
     */
    @Transactional(readOnly = true)
    public Optional<Patient> getPatientByUserId(Long userId) {
        if (userId == null) {
            logger.error("User ID cannot be null");
            throw new IllegalArgumentException("User ID cannot be null");
        }
        logger.info("Fetching patient with user ID: {}", userId);
        return patientRepository.findByUserId(userId);
    }

    /**
     * Creates a new patient in the database.
     * @param patient The patient to create.
     * @return The saved patient.
     * @throws IllegalArgumentException if the patient is null or has an existing email.
     */
    @Transactional
    public Patient createPatient(Patient patient) {
        if (patient == null) {
            logger.error("Patient cannot be null");
            throw new IllegalArgumentException("Patient cannot be null");
        }
        if (patient.getEmail() != null && patientRepository.findByEmail(patient.getEmail()).isPresent()) {
            logger.error("Email {} is already in use", patient.getEmail());
            throw new IllegalArgumentException("Email is already in use");
        }
        logger.info("Creating new patient with email: {}", patient.getEmail());
        return patientRepository.save(patient);
    }

    /**
     * Updates an existing patient with the provided details.
     * @param id The ID of the patient to update.
     * @param patientDetails The updated patient details.
     * @return The updated patient.
     * @throws ResourceNotFoundException if the patient is not found.
     * @throws IllegalArgumentException if the ID or patient details are null, or if the email is already in use.
     */
    @Transactional
    public Patient updatePatient(Long id, Patient patientDetails) {
        if (id == null || patientDetails == null) {
            logger.error("Patient ID or details cannot be null");
            throw new IllegalArgumentException("Patient ID or details cannot be null");
        }
        logger.info("Updating patient with ID: {}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", String.valueOf(id)));

        if (patientDetails.getFirstName() != null) {
            patient.setFirstName(patientDetails.getFirstName());
        }
        if (patientDetails.getLastName() != null) {
            patient.setLastName(patientDetails.getLastName());
        }
        if (patientDetails.getEmail() != null && !patientDetails.getEmail().equals(patient.getEmail())) {
            if (patientRepository.findByEmail(patientDetails.getEmail()).isPresent()) {
                logger.error("Email {} is already in use", patientDetails.getEmail());
                throw new IllegalArgumentException("Email is already in use");
            }
            patient.setEmail(patientDetails.getEmail());
        }
        if (patientDetails.getPhoneNumber() != null) {
            patient.setPhoneNumber(patientDetails.getPhoneNumber());
        }
        if (patientDetails.getMedicalHistory() != null) {
            patient.setMedicalHistory(patientDetails.getMedicalHistory());
        }

        return patientRepository.save(patient);
    }

    /**
     * Deletes a patient from the database.
     * @param id The ID of the patient to delete.
     * @throws ResourceNotFoundException if the patient is not found.
     * @throws IllegalArgumentException if the ID is null.
     */
    @Transactional
    public void deletePatient(Long id) {
        if (id == null) {
            logger.error("Patient ID cannot be null");
            throw new IllegalArgumentException("Patient ID cannot be null");
        }
        logger.info("Deleting patient with ID: {}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", String.valueOf(id)));
        patientRepository.delete(patient);
    }
}