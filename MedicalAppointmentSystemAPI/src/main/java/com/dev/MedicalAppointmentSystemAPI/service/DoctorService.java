package com.dev.MedicalAppointmentSystemAPI.service;

import com.dev.MedicalAppointmentSystemAPI.exception.ResourceNotFoundException;
import com.dev.MedicalAppointmentSystemAPI.model.Doctor;
import com.dev.MedicalAppointmentSystemAPI.repository.DoctorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing Doctor entities.
 * Provides CRUD operations and business logic for doctors in the Medical Appointment System.
 */
@Service
public class DoctorService {

    private static final Logger logger = LoggerFactory.getLogger(DoctorService.class);

    private final DoctorRepository doctorRepository;

    /**
     * Constructs a new DoctorService with the specified DoctorRepository.
     * @param doctorRepository The repository for accessing doctor data.
     */
    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    /**
     * Retrieves all doctors from the database.
     * @return A list of all doctors.
     */
    @Transactional(readOnly = true)
    public List<Doctor> getAllDoctors() {
        logger.info("Fetching all doctors");
        return doctorRepository.findAll();
    }

    /**
     * Retrieves a doctor by their ID.
     * @param id The ID of the doctor.
     * @return An Optional containing the doctor, or empty if not found.
     * @throws IllegalArgumentException if the ID is null.
     */
    @Transactional(readOnly = true)
    public Optional<Doctor> getDoctorById(Long id) {
        if (id == null) {
            logger.error("Doctor ID cannot be null");
            throw new IllegalArgumentException("Doctor ID cannot be null");
        }
        logger.info("Fetching doctor with ID: {}", id);
        return doctorRepository.findById(id);
    }

    /**
     * Retrieves a doctor by their email address.
     * @param email The email address of the doctor.
     * @return An Optional containing the doctor, or empty if not found.
     * @throws IllegalArgumentException if the email is null or blank.
     */
    @Transactional(readOnly = true)
    public Optional<Doctor> getDoctorByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            logger.error("Email cannot be null or empty");
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        logger.info("Fetching doctor with email: {}", email);
        return doctorRepository.findByEmail(email);
    }

    /**
     * Retrieves a doctor by their associated user ID.
     * @param userId The ID of the associated user.
     * @return An Optional containing the doctor, or empty if not found.
     * @throws IllegalArgumentException if the user ID is null.
     */
    @Transactional(readOnly = true)
    public Optional<Doctor> getDoctorByUserId(Long userId) {
        if (userId == null) {
            logger.error("User ID cannot be null");
            throw new IllegalArgumentException("User ID cannot be null");
        }
        logger.info("Fetching doctor with user ID: {}", userId);
        return doctorRepository.findByUserId(userId);
    }

    /**
     * Retrieves doctors by their specialization.
     * @param specialization The specialization to search for.
     * @return A list of doctors with the specified specialization.
     * @throws IllegalArgumentException if the specialization is null or blank.
     */
    @Transactional(readOnly = true)
    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        if (specialization == null || specialization.trim().isEmpty()) {
            logger.error("Specialization cannot be null or empty");
            throw new IllegalArgumentException("Specialization cannot be null or empty");
        }
        logger.info("Fetching doctors with specialization: {}", specialization);
        return doctorRepository.findBySpecialization(specialization);
    }

    /**
     * Creates a new doctor in the database.
     * @param doctor The doctor to create.
     * @return The saved doctor.
     * @throws IllegalArgumentException if the doctor is null or has an existing email.
     */
    @Transactional
    public Doctor createDoctor(Doctor doctor) {
        if (doctor == null) {
            logger.error("Doctor cannot be null");
            throw new IllegalArgumentException("Doctor cannot be null");
        }
        if (doctor.getEmail() != null && doctorRepository.existsByEmail(doctor.getEmail())) {
            logger.error("Email {} is already in use", doctor.getEmail());
            throw new IllegalArgumentException("Email is already in use");
        }
        logger.info("Creating new doctor with email: {}", doctor.getEmail());
        return doctorRepository.save(doctor);
    }

    /**
     * Updates an existing doctor with the provided details.
     * @param id The ID of the doctor to update.
     * @param doctorDetails The updated doctor details.
     * @return The updated doctor.
     * @throws ResourceNotFoundException if the doctor is not found.
     * @throws IllegalArgumentException if the ID or doctor details are null, or if the email is already in use.
     */
    @Transactional
    public Doctor updateDoctor(Long id, Doctor doctorDetails) {
        if (id == null || doctorDetails == null) {
            logger.error("Doctor ID or details cannot be null");
            throw new IllegalArgumentException("Doctor ID or details cannot be null");
        }
        logger.info("Updating doctor with ID: {}", id);
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", String.valueOf(id)));

        if (doctorDetails.getFirstName() != null) {
            doctor.setFirstName(doctorDetails.getFirstName());
        }
        if (doctorDetails.getLastName() != null) {
            doctor.setLastName(doctorDetails.getLastName());
        }
        if (doctorDetails.getEmail() != null && !doctorDetails.getEmail().equals(doctor.getEmail())) {
            if (doctorRepository.existsByEmail(doctorDetails.getEmail())) {
                logger.error("Email {} is already in use", doctorDetails.getEmail());
                throw new IllegalArgumentException("Email is already in use");
            }
            doctor.setEmail(doctorDetails.getEmail());
        }
        if (doctorDetails.getPhoneNumber() != null) {
            doctor.setPhoneNumber(doctorDetails.getPhoneNumber());
        }
        if (doctorDetails.getSpecialization() != null) {
            doctor.setSpecialization(doctorDetails.getSpecialization());
        }
        if (doctorDetails.getQualifications() != null) {
            doctor.setQualifications(doctorDetails.getQualifications());
        }
        if (doctorDetails.getAddress() != null) {
            doctor.setAddress(doctorDetails.getAddress());
        }
        if (doctorDetails.getWorkingHoursStart() != null) {
            doctor.setWorkingHoursStart(doctorDetails.getWorkingHoursStart());
        }
        if (doctorDetails.getWorkingHoursEnd() != null) {
            doctor.setWorkingHoursEnd(doctorDetails.getWorkingHoursEnd());
        }

        return doctorRepository.save(doctor);
    }

    /**
     * Deletes a doctor from the database.
     * @param id The ID of the doctor to delete.
     * @throws ResourceNotFoundException if the doctor is not found.
     * @throws IllegalArgumentException if the ID is null.
     */
    @Transactional
    public void deleteDoctor(Long id) {
        if (id == null) {
            logger.error("Doctor ID cannot be null");
            throw new IllegalArgumentException("Doctor ID cannot be null");
        }
        logger.info("Deleting doctor with ID: {}", id);
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", String.valueOf(id)));
        doctorRepository.delete(doctor);
    }
}