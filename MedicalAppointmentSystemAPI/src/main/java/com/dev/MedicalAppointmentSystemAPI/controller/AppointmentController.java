package com.dev.MedicalAppointmentSystemAPI.controller;

import com.dev.MedicalAppointmentSystemAPI.entity.Appointment;
import com.dev.MedicalAppointmentSystemAPI.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

	@Autowired
	private AppointmentService appointmentService;

	// Create a new appointment
	@PostMapping ("/createappointment")
	public String createAppointment(@RequestBody Appointment appointment) {
		return appointmentService.createAppointment(appointment);
	}

	// Update an appointment with new doctor name and status
	@PutMapping("/updateappointment")
	public String updateDoctorAndStatus(@RequestParam Long id, @RequestParam String doctorName,
			@RequestParam boolean status) {
		return appointmentService.updateDoctorAndStatus(id, doctorName, status);
	}

	// Delete an appointment by ID
	@DeleteMapping("/delbyid")
	public String deleteAppointmentById(@RequestParam Long id) {
		appointmentService.deleteAppointmentById(id);
		return "Appointment with ID " + id + " has been deleted.";
	}

	// Delete appointments whose status is cancelled
	@DeleteMapping("/bystatus")
	public String deleteCancelledAppointments() {
		appointmentService.deleteCancelledAppointments();
		return "All cancelled appointments have been deleted.";
	}

	// Get an appointment by ID
	@GetMapping("/byid")
	public Optional<Appointment> getAppointmentById(@RequestParam Long id) {
		return appointmentService.getAppointmentById(id);
	}

	// Get appointments by doctor name
	@GetMapping("/docbyname")
	public List<Appointment> getAppointmentsByDoctorName(@RequestParam String doctorName) {
		return appointmentService.getAppointmentsByDoctorName(doctorName);
	}

	// Get appointments by doctor name and date
	@GetMapping("/docbynameAndDate")
	public List<Appointment> getAppointmentsByDoctorNameAndDate(@RequestParam String doctorName,
			@RequestParam String date) {
		return appointmentService.findByDoctorNameAndAppointmentDate(doctorName, date);
	}

	// Get confirmed appointments
	@GetMapping("/getbystatus")
	public List<Appointment> getConfirmedAppointments() {
		return appointmentService.getConfirmedAppointments();
	}

	// Get appointments by date
	@GetMapping("/getbydate")
	public List<Appointment> getAppointmentsByDate(@RequestParam String date) {
		return appointmentService.getAppointmentsByDate(date);
	}

	// Update patient name and appointment date based on ID
	@PutMapping("/updatebyid")
	public String updatePatientAndDate(@RequestParam Long id, @RequestParam String patientName,
			@RequestParam String appointmentDate) {
		return appointmentService.updatePatientAndDate(id, patientName, appointmentDate);
	}
}
