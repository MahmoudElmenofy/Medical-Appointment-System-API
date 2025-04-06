package com.dev.MedicalAppointmentSystemAPI.service;


import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.dev.MedicalAppointmentSystemAPI.entity.Appointment;

@Repository
public interface AppointmentService {
	// Create appointments
	String createAppointment(Appointment appointment);

	// Retrieve appointments by Appointment ID
	Optional<Appointment> getAppointmentById(Long id);

	// Update doctor name and appointment status
	String updateDoctorAndStatus(Long id, String doctorName, boolean status);

	// Delete appointment based on ID
	void deleteAppointmentById(Long id);

	// Delete appointments whose status is cancelled
	void deleteCancelledAppointments();

	// Retrieve appointments by doctor name
	List<Appointment> getAppointmentsByDoctorName(String doctorName);

	// Find appointments on a specific date
	List<Appointment> getAppointmentsByDate(String date);

	// Display all confirmed appointments
	List<Appointment> getConfirmedAppointments();

	//	Display by Doctor name and appointment Date
	List<Appointment> findByDoctorNameAndAppointmentDate(String doctorName, String date);

	// Update patient name and appointment date
	String updatePatientAndDate(Long id, String patientName, String appointmentDate);

}
