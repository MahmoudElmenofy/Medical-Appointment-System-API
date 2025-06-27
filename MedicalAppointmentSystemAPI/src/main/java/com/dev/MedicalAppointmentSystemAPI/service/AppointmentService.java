package com.dev.MedicalAppointmentSystemAPI.service;

import com.dev.MedicalAppointmentSystemAPI.model.Appointment;
import com.dev.MedicalAppointmentSystemAPI.model.AppointmentStatus;
import com.dev.MedicalAppointmentSystemAPI.model.Doctor;
import com.dev.MedicalAppointmentSystemAPI.model.Patient;
import com.dev.MedicalAppointmentSystemAPI.repository.AppointmentRepository;
import com.dev.MedicalAppointmentSystemAPI.repository.DoctorRepository;
import com.dev.MedicalAppointmentSystemAPI.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

	private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);

	private final AppointmentRepository appointmentRepository;
	private final PatientRepository patientRepository;
	private final DoctorRepository doctorRepository;

	public AppointmentService(AppointmentRepository appointmentRepository,
							  PatientRepository patientRepository,
							  DoctorRepository doctorRepository) {
		this.appointmentRepository = appointmentRepository;
		this.patientRepository = patientRepository;
		this.doctorRepository = doctorRepository;
	}

	@Transactional(readOnly = true)
	public List<Appointment> getAllAppointments() {
		logger.info("Fetching all appointments");
		return appointmentRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Optional<Appointment> getAppointmentById(Long id) {
		if (id == null) {
			logger.error("Appointment ID cannot be null");
			throw new IllegalArgumentException("Appointment ID cannot be null");
		}
		logger.info("Fetching appointment with ID: {}", id);
		return appointmentRepository.findById(id);
	}

	@Transactional(readOnly = true)
	public List<Appointment> getAppointmentsByPatientId(Long patientId) {
		if (patientId == null) {
			logger.error("Patient ID cannot be null");
			throw new IllegalArgumentException("Patient ID cannot be null");
		}
		logger.info("Fetching appointments for patient ID: {}", patientId);
		return appointmentRepository.findByPatientId(patientId);
	}

	@Transactional(readOnly = true)
	public List<Appointment> getAppointmentsByDoctorId(Long doctorId) {
		if (doctorId == null) {
			logger.error("Doctor ID cannot be null");
			throw new IllegalArgumentException("Doctor ID cannot be null");
		}
		logger.info("Fetching appointments for doctor ID: {}", doctorId);
		return appointmentRepository.findByDoctorId(doctorId);
	}

	@Transactional(readOnly = true)
	public List<Appointment> getAppointmentsByStatus(AppointmentStatus status) {
		if (status == null) {
			logger.error("Status cannot be null");
			throw new IllegalArgumentException("Status cannot be null");
		}
		logger.info("Fetching appointments with status: {}", status);
		return appointmentRepository.findByStatus(status);
	}

	@Transactional(readOnly = true)
	public List<Appointment> getAppointmentsByDoctorAndDateRange(Long doctorId, LocalDateTime startDate, LocalDateTime endDate) {
		validateDateRange(startDate, endDate);
		if (doctorId == null) {
			logger.error("Doctor ID cannot be null");
			throw new IllegalArgumentException("Doctor ID cannot be null");
		}
		logger.info("Fetching appointments for doctor ID: {} between {} and {}", doctorId, startDate, endDate);
		return appointmentRepository.findByDoctorIdAndDateRange(doctorId, startDate, endDate);
	}

	@Transactional(readOnly = true)
	public List<Appointment> getAppointmentsByPatientAndDateRange(Long patientId, LocalDateTime startDate, LocalDateTime endDate) {
		validateDateRange(startDate, endDate);
		if (patientId == null) {
			logger.error("Patient ID cannot be null");
			throw new IllegalArgumentException("Patient ID cannot be null");
		}
		logger.info("Fetching appointments for patient ID: {} between {} and {}", patientId, startDate, endDate);
		return appointmentRepository.findByPatientIdAndDateRange(patientId, startDate, endDate);
	}

	@Transactional
	public Appointment createAppointment(Long patientId, Long doctorId, Appointment appointment) {
		if (patientId == null || doctorId == null || appointment == null) {
			logger.error("Patient ID, Doctor ID, or Appointment cannot be null");
			throw new IllegalArgumentException("Patient ID, Doctor ID, or Appointment cannot be null");
		}

		logger.info("Creating appointment for patient ID: {} and doctor ID: {}", patientId, doctorId);
		Patient patient = patientRepository.findById(patientId)
				.orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientId));
		Doctor doctor = doctorRepository.findById(doctorId)
				.orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + doctorId));

		if (appointment.getAppointmentDateTime() == null || appointment.getAppointmentDateTime().isBefore(LocalDateTime.now())) {
			logger.error("Appointment date must be in the future");
			throw new IllegalArgumentException("Appointment date must be in the future");
		}

		appointment.setPatient(patient);
		appointment.setDoctor(doctor);
		appointment.setStatus(AppointmentStatus.SCHEDULED);
		appointment.setCreatedAt(LocalDateTime.now());
		appointment.setUpdatedAt(LocalDateTime.now());

		return appointmentRepository.save(appointment);
	}

	@Transactional
	public Appointment updateAppointment(Long id, Appointment appointmentDetails) {
		if (id == null || appointmentDetails == null) {
			logger.error("Appointment ID or details cannot be null");
			throw new IllegalArgumentException("Appointment ID or details cannot be null");
		}

		logger.info("Updating appointment with ID: {}", id);
		Appointment appointment = appointmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));

		if (appointmentDetails.getAppointmentDateTime() != null) {
			if (appointmentDetails.getAppointmentDateTime().isBefore(LocalDateTime.now())) {
				logger.error("Appointment date must be in the future");
				throw new IllegalArgumentException("Appointment date must be in the future");
			}
			appointment.setAppointmentDateTime(appointmentDetails.getAppointmentDateTime());
		}
		if (appointmentDetails.getEndDateTime() != null) {
			appointment.setEndDateTime(appointmentDetails.getEndDateTime());
		}
		if (appointmentDetails.getStatus() != null) {
			appointment.setStatus(appointmentDetails.getStatus());
		}
		if (appointmentDetails.getReason() != null) {
			appointment.setReason(appointmentDetails.getReason());
		}
		if (appointmentDetails.getNotes() != null) {
			appointment.setNotes(appointmentDetails.getNotes());
		}

		appointment.setUpdatedAt(LocalDateTime.now());
		return appointmentRepository.save(appointment);
	}

	@Transactional
	public void deleteAppointment(Long id) {
		if (id == null) {
			logger.error("Appointment ID cannot be null");
			throw new IllegalArgumentException("Appointment ID cannot be null");
		}

		logger.info("Deleting appointment with ID: {}", id);
		Appointment appointment = appointmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));
		appointmentRepository.delete(appointment);
	}

	@Transactional
	public Appointment updateAppointmentStatus(Long id, AppointmentStatus status) {
		if (id == null || status == null) {
			logger.error("Appointment ID or status cannot be null");
			throw new IllegalArgumentException("Appointment ID or status cannot be null");
		}

		logger.info("Updating appointment ID: {} to status: {}", id, status);
		Appointment appointment = appointmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));
		appointment.setStatus(status);
		appointment.setUpdatedAt(LocalDateTime.now());
		return appointmentRepository.save(appointment);
	}

	private void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
		if (startDate == null || endDate == null) {
			logger.error("Start date or end date cannot be null");
			throw new IllegalArgumentException("Start date or end date cannot be null");
		}
		if (startDate.isAfter(endDate)) {
			logger.error("Start date cannot be after end date");
			throw new IllegalArgumentException("Start date cannot be after end date");
		}
	}

	// Custom exception class
	public static class ResourceNotFoundException extends RuntimeException {
		public ResourceNotFoundException(String message) {
			super(message);
		}
	}
}