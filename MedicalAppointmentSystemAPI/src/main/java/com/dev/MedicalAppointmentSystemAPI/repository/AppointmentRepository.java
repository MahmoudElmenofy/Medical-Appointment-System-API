package com.dev.MedicalAppointmentSystemAPI.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dev.MedicalAppointmentSystemAPI.entity.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query(value = "SELECT * FROM appointment WHERE status = :status", nativeQuery = true)
    List<Appointment> findByStatus(boolean status);

    @Query(value = "SELECT * FROM appointment WHERE doctor_Name = :doctorName", nativeQuery = true)
    List<Appointment> findByDoctorName(String doctorName);

    @Query(value = "SELECT * FROM appointment WHERE appointment_date = :date", nativeQuery = true)
    List<Appointment> findByAppointmentDate(String date);

    @Query(value = "SELECT * FROM appointment WHERE doctor_Name = :doctorName AND appointment_date = :date", nativeQuery = true)
    List<Appointment> findByDoctorNameAndAppointmentDate(@Param("doctorName") String doctorName, 
            @Param("date") String date);
}
