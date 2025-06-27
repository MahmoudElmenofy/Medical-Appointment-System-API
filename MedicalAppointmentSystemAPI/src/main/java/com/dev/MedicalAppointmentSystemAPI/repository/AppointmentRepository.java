package com.dev.MedicalAppointmentSystemAPI.repository;

import com.dev.MedicalAppointmentSystemAPI.model.Appointment;
import com.dev.MedicalAppointmentSystemAPI.model.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for managing Appointment entities.
 * Provides methods for querying appointments by patient, doctor, status, and date ranges.
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

        /**
         * Finds all appointments for a given patient ID.
         * @param patientId The ID of the patient.
         * @return A list of appointments.
         */
        List<Appointment> findByPatientId(Long patientId);

        /**
         * Finds all appointments for a given patient ID with pagination.
         * @param patientId The ID of the patient.
         * @param pageable Pagination information.
         * @return A page of appointments.
         */
        Page<Appointment> findByPatientId(Long patientId, Pageable pageable);

        /**
         * Finds all appointments for a given doctor ID.
         * @param doctorId The ID of the doctor.
         * @return A list of appointments.
         */
        List<Appointment> findByDoctorId(Long doctorId);

        /**
         * Finds all appointments for a given doctor ID with pagination.
         * @param doctorId The ID of the doctor.
         * @param pageable Pagination information.
         * @return A page of appointments.
         */
        Page<Appointment> findByDoctorId(Long doctorId, Pageable pageable);

        /**
         * Finds all appointments with a given status.
         * @param status The appointment status.
         * @return A list of appointments.
         */
        List<Appointment> findByStatus(AppointmentStatus status);

        /**
         * Finds all appointments with a given status with pagination.
         * @param status The appointment status.
         * @param pageable Pagination information.
         * @return A page of appointments.
         */
        Page<Appointment> findByStatus(AppointmentStatus status, Pageable pageable);

        /**
         * Finds appointments for a given doctor ID within a date range.
         * @param doctorId The ID of the doctor.
         * @param startDate The start of the date range.
         * @param endDate The end of the date range.
         * @return A list of appointments.
         */
        @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDateTime BETWEEN :startDate AND :endDate")
        List<Appointment> findByDoctorIdAndDateRange(@Param("doctorId") Long doctorId,
                                                     @Param("startDate") LocalDateTime startDate,
                                                     @Param("endDate") LocalDateTime endDate);

        /**
         * Finds appointments for a given doctor ID within a date range with pagination.
         * @param doctorId The ID of the doctor.
         * @param startDate The start of the date range.
         * @param endDate The end of the date range.
         * @param pageable Pagination information.
         * @return A page of appointments.
         */
        @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDateTime BETWEEN :startDate AND :endDate")
        Page<Appointment> findByDoctorIdAndDateRange(@Param("doctorId") Long doctorId,
                                                     @Param("startDate") LocalDateTime startDate,
                                                     @Param("endDate") LocalDateTime endDate,
                                                     Pageable pageable);

        /**
         * Finds appointments for a given patient ID within a date range.
         * @param patientId The ID of the patient.
         * @param startDate The start of the date range.
         * @param endDate The end of the date range.
         * @return A list of appointments.
         */
        @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND a.appointmentDateTime BETWEEN :startDate AND :endDate")
        List<Appointment> findByPatientIdAndDateRange(@Param("patientId") Long patientId,
                                                      @Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate);

        /**
         * Finds appointments for a given patient ID within a date range with pagination.
         * @param patientId The ID of the patient.
         * @param startDate The start of the date range.
         * @param endDate The end of the date range.
         * @param pageable Pagination information.
         * @return A page of appointments.
         */
        @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND a.appointmentDateTime BETWEEN :startDate AND :endDate")
        Page<Appointment> findByPatientIdAndDateRange(@Param("patientId") Long patientId,
                                                      @Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate,
                                                      Pageable pageable);
        /**
         * Finds appointments by patient ID and doctor ID.
         * @param patientId The ID of the patient.
         * @param doctorId The ID of the doctor.
         * @return A list of appointments matching the criteria.
         */
        List<Appointment> findByPatientIdAndDoctorId(Long patientId, Long doctorId);

        /**
         * Checks if an appointment exists for the specified patient and doctor.
         * @param patientId The ID of the patient.
         * @param doctorId The ID of the doctor.
         * @return true if an appointment exists, false otherwise.
         */
        boolean existsByPatientIdAndDoctorId(Long patientId, Long doctorId);
}