package com.dev.MedicalAppointmentSystemAPI.controller;

import com.dev.MedicalAppointmentSystemAPI.model.Doctor;
import com.dev.MedicalAppointmentSystemAPI.service.DoctorService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Doctor entities in the Medical Appointment System.
 * Provides endpoints for CRUD operations on doctors, secured with role-based access control.
 */
@RestController
@RequestMapping("/api/v1/doctors")
@CrossOrigin(origins = "*", maxAge = 3600) // TODO: Tighten CORS in production
public class DoctorController {

    private final DoctorService doctorService;

    /**
     * Constructs a new DoctorController with the specified DoctorService.
     * @param doctorService The service for doctor operations.
     */
    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    /**
     * Retrieves all doctors.
     * Requires ADMIN or DOCTOR role.
     * @return ResponseEntity with a list of doctors and HTTP 200 status.
     * Example: GET /api/v1/doctors
     * Response: [{"id":1,"firstName":"John","lastName":"Doe","email":"john@example.com",...},...]
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        List<Doctor> doctors = doctorService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }

    /**
     * Retrieves a doctor by their ID.
     * Requires PATIENT, DOCTOR, or ADMIN role.
     * @param id The ID of the doctor (non-null, positive).
     * @return ResponseEntity with the doctor and HTTP 200 status, or 404 if not found.
     * Example: GET /api/v1/doctors/1
     * Response: {"id":1,"firstName":"John","lastName":"Doe","email":"john@example.com",...}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable @NotNull(message = "Doctor ID cannot be null") Long id) {
        Doctor doctor = doctorService.getDoctorById(id)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + id));
        return ResponseEntity.ok(doctor);
    }

    /**
     * Retrieves a doctor by their email address (case-insensitive).
     * Requires ADMIN role.
     * @param email The email address of the doctor (non-null, non-empty).
     * @return ResponseEntity with the doctor and HTTP 200 status, or 404 if not found.
     * Example: GET /api/v1/doctors/email/john@example.com
     * Response: {"id":1,"firstName":"John","lastName":"Doe","email":"john@example.com",...}
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Doctor> getDoctorByEmail(@PathVariable @NotBlank(message = "Email cannot be blank") String email) {
        Doctor doctor = doctorService.getDoctorByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with email: " + email));
        return ResponseEntity.ok(doctor);
    }

    /**
     * Retrieves a doctor by their associated user ID.
     * Requires ADMIN role or DOCTOR role with user ownership.
     * @param userId The ID of the associated user (non-null, positive).
     * @return ResponseEntity with the doctor and HTTP 200 status, or 404 if not found.
     * Example: GET /api/v1/doctors/user/1
     * Response: {"id":1,"firstName":"John","lastName":"Doe","email":"john@example.com",...}
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('DOCTOR') and @doctorSecurity.isUserOwner(#userId, principal))")
    public ResponseEntity<Doctor> getDoctorByUserId(@PathVariable @NotNull(message = "User ID cannot be null") Long userId) {
        Doctor doctor = doctorService.getDoctorByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with user ID: " + userId));
        return ResponseEntity.ok(doctor);
    }

    /**
     * Retrieves doctors by their specialization (case-insensitive).
     * Requires PATIENT, DOCTOR, or ADMIN role.
     * @param specialization The specialization to search for (non-null, non-empty).
     * @return ResponseEntity with a list of doctors and HTTP 200 status.
     * Example: GET /api/v1/doctors/specialization/Cardiology
     * Response: [{"id":1,"firstName":"John","lastName":"Doe","specialization":"Cardiology",...},...]
     */
    @GetMapping("/specialization/{specialization}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<List<Doctor>> getDoctorsBySpecialization(@PathVariable @NotBlank(message = "Specialization cannot be blank") String specialization) {
        List<Doctor> doctors = doctorService.getDoctorsBySpecialization(specialization);
        return ResponseEntity.ok(doctors);
    }

    /**
     * Creates a new doctor.
     * Requires DOCTOR or ADMIN role.
     * @param doctor The doctor details (validated).
     * @return ResponseEntity with the created doctor and HTTP 201 status.
     * Example: POST /api/v1/doctors
     * Request: {"firstName":"John","lastName":"Doe","email":"john@example.com","specialization":"Cardiology"}
     * Response: {"id":1,"firstName":"John","lastName":"Doe","email":"john@example.com",...}
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<Doctor> createDoctor(@Valid @RequestBody Doctor doctor) {
        Doctor newDoctor = doctorService.createDoctor(doctor);
        return ResponseEntity.status(201).body(newDoctor);
    }

    /**
     * Updates an existing doctor.
     * Requires ADMIN role or DOCTOR role with ownership.
     * @param id The ID of the doctor to update (non-null, positive).
     * @param doctorDetails The updated doctor details (validated).
     * @return ResponseEntity with the updated doctor and HTTP 200 status, or 404 if not found.
     * Example: PUT /api/v1/doctors/1
     * Request: {"firstName":"John","lastName":"Doe","email":"john.doe@example.com"}
     * Response: {"id":1,"firstName":"John","lastName":"Doe","email":"john.doe@example.com",...}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('DOCTOR') and @doctorSecurity.isDoctorOwner(#id, principal))")
    public ResponseEntity<Doctor> updateDoctor(@PathVariable @NotNull(message = "Doctor ID cannot be null") Long id, @Valid @RequestBody Doctor doctorDetails) {
        Doctor updatedDoctor = doctorService.updateDoctor(id, doctorDetails);
        return ResponseEntity.ok(updatedDoctor);
    }

    /**
     * Deletes a doctor.
     * Requires ADMIN role.
     * @param id The ID of the doctor to delete (non-null, positive).
     * @return ResponseEntity with HTTP 204 status, or 404 if not found.
     * Example: DELETE /api/v1/doctors/1
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDoctor(@PathVariable @NotNull(message = "Doctor ID cannot be null") Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }
}