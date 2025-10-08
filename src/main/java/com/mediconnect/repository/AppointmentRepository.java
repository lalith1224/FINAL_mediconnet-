package com.mediconnect.repository;

import com.mediconnect.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    
    List<Appointment> findByPatientId(UUID patientId);
    
    List<Appointment> findByDoctorId(UUID doctorId);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND CAST(a.appointmentDate AS DATE) = :date")
    List<Appointment> findByDoctorIdAndDate(@Param("doctorId") UUID doctorId, @Param("date") LocalDate date);
    
    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND a.appointmentDate >= :startDate ORDER BY a.appointmentDate")
    List<Appointment> findUpcomingByPatientId(@Param("patientId") UUID patientId, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate >= :startDate ORDER BY a.appointmentDate")
    List<Appointment> findUpcomingByDoctorId(@Param("doctorId") UUID doctorId, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId AND CAST(a.appointmentDate AS DATE) = :date")
    long countByDoctorIdAndDate(@Param("doctorId") UUID doctorId, @Param("date") LocalDate date);
    
    List<Appointment> findByStatus(Appointment.AppointmentStatus status);
}
