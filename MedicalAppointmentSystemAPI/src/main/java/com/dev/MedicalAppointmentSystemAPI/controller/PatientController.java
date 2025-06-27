package com.dev.MedicalAppointmentSystemAPI.controller;

import com.dev.MedicalAppointmentSystemAPI.model.Patient;
import com.dev.MedicalAppointmentSystemAPI.service.PatientService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Patient entities in the Medical Appointment System.
 * Provides endpoints for CRUD operations on patients, secured with role-based access control.
 */
@RestController
@RequestMapping("/api/v1/patients")
@CrossOrigin(origins = "*", maxAge = 3600) // TODO: Tighten CORS in production
public class PatientController {

    private final PatientService patientService;

    /**
     * Constructs a new PatientController with the specified PatientService.
     * @param patientService The service for patient operations.
     */
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    /**
     * Retrieves all patients.
     * Requires ADMIN role.
     * @return ResponseEntity with a list of patients and HTTP 200 status.
     * Example: GET /api/v1/patients
     * Response: [{"id":1,"firstName":"John","lastName":"Doe","email":"john@example.com",...},...]
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Patient>> getAllPatients() {
        List<Patient> patients = patientService.getAllPatients();
        return ResponseEntity.ok(patients);
    }

    /**
     * Retrieves a patient by their ID.
     * Requires ADMIN role, or PATIENT role with ownership, or DOCTOR role with assignment.
     * @param id The ID of the patient (non-null, positive).
     * @return ResponseEntity with the patient and HTTP 200 status, or 404 if not found.
     * Example: GET /api/v1/patients/1
     * Response: {"id":1,"firstName":"John","lastName":"Doe","email":"john@example.com",...}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PATIENT') and @patientSecurity.isPatientOwner(#id, principal)) or (hasRole('DOCTOR') and @patientSecurity.isDoctorAssignedToPatient(#id, principal))")
    public ResponseEntity<Patient> getPatientById(@PathVariable @NotNull(message = "Patient ID cannot be null") Long id) {
        Patient patient = patientService.getPatientById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + id));
        return ResponseEntity.ok(patient);
    }

    /**
     * Retrieves a patient by their email address (case-insensitive).
     * Requires ADMIN role.
     * @param email The email address of the patient (non-null, non-empty).
     * @return ResponseEntity with the patient and HTTP 200 status, or 404 if not found.
     * Example: GET /api/v1/patients/email/john@example.com
     * Response: {"id":1,"firstName":"John","lastName":"Doe","email":"john@example.com",...}
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Patient> getPatientByEmail(@PathVariable @NotBlank(message = "Email cannot be blank") String email) {
        Patient patient = patientService.getPatientByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with email: " + email));
        return ResponseEntity.ok(patient);
    }

    /**
     * Retrieves a patient by their associated user ID.
     * Requires ADMIN role or PATIENT role with user ownership.
     * @param userId The ID of the associated user (non-null, positive).
     * @return ResponseEntity with the patient and HTTP 200 status, or 404 if not found.
     * Example: GET /api/v1/patients/user/1
     * Response: {"id":1,"firstName":"John","lastName":"Doe","email":"john@example.com",...}
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PATIENT') and @patientSecurity.isUserOwner(#userId, principal))")
    public ResponseEntity<Patient> getPatientByUserId(@PathVariable @NotNull(message = "User ID cannot be null") Long userId) {
        Patient patient = patientService.getPatientByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with user ID: " + userId));
        return ResponseEntity.ok(patient);
    }

    /**
     * Creates a new patient.
     * Requires PATIENT or ADMIN role.
     * @param patient The patient details (validated).
     * @return ResponseEntity with the created patient and HTTP 201 status.
     * Example: POST /api/v1/patients
     * Request: {"firstName":"John","lastName":"Doe","email":"john@example.com","phoneNumber":"+1234567890"}
     * Response: {"id":1,"firstName":"John","lastName":"Doe","email":"john@example.com",...}
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    public ResponseEntity<Patient> createPatient(@Valid @RequestBody Patient patient) {
        Patient newPatient = patientService.createPatient(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPatient);
    }

    /**
     * Updates an existing patient.
     * Requires ADMIN role or PATIENT role with ownership.
     * @param id The ID of the patient to update (non-null, positive).
     * @param patientDetails The updated patient details (validated).
     * @return ResponseEntity with the updated patient and HTTP 200 status, or 404 if not found.
     * Example: PUT /api/v1/patients/1
     * Request: {"firstName":"John","lastName":"Doe","email":"john.doe@example.com"}
     * Response: {"id":1,"firstName":"John","lastName":"Doe","email":"john.doe@example.com",...}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PATIENT') and @patientSecurity.isPatientOwner(#id, principal))")
    public ResponseEntity<Patient> updatePatient(@PathVariable @NotNull(message = "Patient ID cannot be null") Long id, @Valid @RequestBody Patient patientDetails) {
        Patient updatedPatient = patientService.updatePatient(id, patientDetails);
        return ResponseEntity.ok(updatedPatient);
    }

    /**
     * Deletes a patient.
     * Requires ADMIN role.
     * @param id The ID of the patient to delete (non-null, positive).
     * @return ResponseEntity with HTTP 204 status, or 404 if not found.
     * Example: DELETE /api/v1/patients/1
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePatient(@PathVariable @NotNull(message = "Patient ID cannot be null") Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}