package com.dev.MedicalAppointmentSystemAPI.controller;

import com.dev.MedicalAppointmentSystemAPI.model.Appointment;
import com.dev.MedicalAppointmentSystemAPI.model.AppointmentStatus;
import com.dev.MedicalAppointmentSystemAPI.service.AppointmentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AppointmentController {

	private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);

	private final AppointmentService appointmentService;

	public AppointmentController(AppointmentService appointmentService) {
		this.appointmentService = appointmentService;
	}

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<Appointment>> getAllAppointments() {
		logger.info("Fetching all appointments");
		List<Appointment> appointments = appointmentService.getAllAppointments();
		return new ResponseEntity<>(appointments, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
	public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id) {
		logger.info("Fetching appointment with ID: {}", id);
		return appointmentService.getAppointmentById(id)
				.map(appointment -> new ResponseEntity<>(appointment, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/patient/{patientId}")
	@PreAuthorize("hasAnyRole('PATIENT', 'ADMIN') or (hasRole('DOCTOR') and @appointmentSecurity.isPatientAssignedToDoctor(#patientId, principal))")
	public ResponseEntity<List<Appointment>> getAppointmentsByPatientId(@PathVariable Long patientId) {
		logger.info("Fetching appointments for patient ID: {}", patientId);
		List<Appointment> appointments = appointmentService.getAppointmentsByPatientId(patientId);
		return new ResponseEntity<>(appointments, HttpStatus.OK);
	}

	@GetMapping("/doctor/{doctorId}")
	@PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN') or (hasRole('PATIENT') and @appointmentSecurity.isDoctorAssignedToPatient(#doctorId, principal))")
	public ResponseEntity<List<Appointment>> getAppointmentsByDoctorId(@PathVariable Long doctorId) {
		logger.info("Fetching appointments for doctor ID: {}", doctorId);
		List<Appointment> appointments = appointmentService.getAppointmentsByDoctorId(doctorId);
		return new ResponseEntity<>(appointments, HttpStatus.OK);
	}

	@GetMapping("/status/{status}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<Appointment>> getAppointmentsByStatus(@PathVariable AppointmentStatus status) {
		logger.info("Fetching appointments with status: {}", status);
		List<Appointment> appointments = appointmentService.getAppointmentsByStatus(status);
		return new ResponseEntity<>(appointments, HttpStatus.OK);
	}

	@GetMapping("/doctor/{doctorId}/date-range")
	@PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
	public ResponseEntity<List<Appointment>> getAppointmentsByDoctorAndDateRange(
			@PathVariable Long doctorId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
		logger.info("Fetching appointments for doctor ID: {} between {} and {}", doctorId, startDate, endDate);
		List<Appointment> appointments = appointmentService.getAppointmentsByDoctorAndDateRange(doctorId, startDate, endDate);
		return new ResponseEntity<>(appointments, HttpStatus.OK);
	}

	@GetMapping("/patient/{patientId}/date-range")
	@PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
	public ResponseEntity<List<Appointment>> getAppointmentsByPatientAndDateRange(
			@PathVariable Long patientId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
		logger.info("Fetching appointments for patient ID: {} between {} and {}", patientId, startDate, endDate);
		List<Appointment> appointments = appointmentService.getAppointmentsByPatientAndDateRange(patientId, startDate, endDate);
		return new ResponseEntity<>(appointments, HttpStatus.OK);
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
	public ResponseEntity<Appointment> createAppointment(
			@RequestParam Long patientId,
			@RequestParam Long doctorId,
			@Valid @RequestBody Appointment appointment) {
		logger.info("Creating appointment for patient ID: {} and doctor ID: {}", patientId, doctorId);
		Appointment newAppointment = appointmentService.createAppointment(patientId, doctorId, appointment);
		return new ResponseEntity<>(newAppointment, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN') or (hasRole('PATIENT') and @appointmentSecurity.isAppointmentOwner(#id, principal))")
	public ResponseEntity<Appointment> updateAppointment(@PathVariable Long id, @Valid @RequestBody Appointment appointmentDetails) {
		logger.info("Updating appointment with ID: {}", id);
		Appointment updatedAppointment = appointmentService.updateAppointment(id, appointmentDetails);
		return new ResponseEntity<>(updatedAppointment, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
		logger.info("Deleting appointment with ID: {}", id);
		appointmentService.deleteAppointment(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PatchMapping("/{id}/cancel")
	@PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
	public ResponseEntity<Appointment> cancelAppointment(@PathVariable Long id) {
		logger.info("Cancelling appointment with ID: {}", id);
		Appointment cancelledAppointment = appointmentService.updateAppointmentStatus(id, AppointmentStatus.CANCELLED);
		return new ResponseEntity<>(cancelledAppointment, HttpStatus.OK);
	}

	@PatchMapping("/{id}/confirm")
	@PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
	public ResponseEntity<Appointment> confirmAppointment(@PathVariable Long id) {
		logger.info("Confirming appointment with ID: {}", id);
		Appointment confirmedAppointment = appointmentService.updateAppointmentStatus(id, AppointmentStatus.CONFIRMED);
		return new ResponseEntity<>(confirmedAppointment, HttpStatus.OK);
	}

	@PatchMapping("/{id}/complete")
	@PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
	public ResponseEntity<Appointment> completeAppointment(@PathVariable Long id) {
		logger.info("Completing appointment with ID: {}", id);
		Appointment completedAppointment = appointmentService.updateAppointmentStatus(id, AppointmentStatus.COMPLETED);
		return new ResponseEntity<>(completedAppointment, HttpStatus.OK);
	}

	// Exception handler for ResourceNotFoundException
	@ExceptionHandler(AppointmentService.ResourceNotFoundException.class)
	public ResponseEntity<String> handleResourceNotFound(AppointmentService.ResourceNotFoundException ex) {
		logger.error("Resource not found: {}", ex.getMessage());
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	// Exception handler for IllegalArgumentException
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
		logger.error("Invalid argument: {}", ex.getMessage());
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}
}