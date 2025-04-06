package com.dev.MedicalAppointmentSystemAPI.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dev.MedicalAppointmentSystemAPI.entity.Appointment;
import com.dev.MedicalAppointmentSystemAPI.repository.AppointmentRepository;

@Service
public class AppointmentServiceImpl implements AppointmentService {

	@Autowired
	AppointmentRepository appointmentrepo;

	@Override
	public String createAppointment(Appointment appointment) {
		appointmentrepo.save(appointment);
		return "Appointment Created Successfully...";
	}

	@Override
	public Optional<Appointment> getAppointmentById(Long id) {
		return appointmentrepo.findById(id);
	}

	@Override
	public String updateDoctorAndStatus(Long id, String doctorName, boolean status) {
		Optional<Appointment> optionalAppointment = appointmentrepo.findById(id);
		if (optionalAppointment.isPresent()) {
			Appointment appointment = optionalAppointment.get();
			appointment.setDoctorName(doctorName);
			appointment.setStatus(status);
			appointmentrepo.save(appointment);
			return "Doctor Name and Status is Updated...";
		} else {
			return id + "Not Found";
		}

	}

	@Override
	public void deleteAppointmentById(Long id) {
		appointmentrepo.deleteById(id);

	}

	@Override
	public void deleteCancelledAppointments() {
		List<Appointment> cancelledAppointments = appointmentrepo.findByStatus(false);
		appointmentrepo.deleteAll(cancelledAppointments);

	}

	@Override
	public List<Appointment> getAppointmentsByDoctorName(String doctorName) {
		return appointmentrepo.findByDoctorName(doctorName);
	}

	@Override
	public List<Appointment> getAppointmentsByDate(String date) {
		return appointmentrepo.findByAppointmentDate(date);
	}

	@Override
	public List<Appointment> getConfirmedAppointments() {
		return appointmentrepo.findByStatus(true);
	}

	@Override
	public String updatePatientAndDate(Long id, String patientName, String appointmentDate) {
		Optional<Appointment> optionalAppointment = appointmentrepo.findById(id);
		if (optionalAppointment.isPresent()) {
			Appointment appointment = optionalAppointment.get();
			appointment.setPatientName(patientName);
			appointment.setAppointmentDate(appointmentDate);
			appointmentrepo.save(appointment);
			return "Patient name and Date of apointment is Updated....";
		} else {
			return "Patient name and Date of apointment is Faild!...";
		}

	}

	@Override
	public List<Appointment> findByDoctorNameAndAppointmentDate(String doctorName, String date) {
		return appointmentrepo.findByDoctorNameAndAppointmentDate(doctorName, date);
	}

}
