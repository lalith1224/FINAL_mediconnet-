package com.mediconnect.repository;

import com.mediconnect.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    
    Optional<Patient> findByUserId(UUID userId);
    
    @Query("SELECT p FROM Patient p WHERE p.user.email = :email")
    Optional<Patient> findByUserEmail(@Param("email") String email);
    
    @Query("SELECT p FROM Patient p JOIN p.appointments a WHERE a.doctor.id = :doctorId")
    List<Patient> findByDoctorId(@Param("doctorId") UUID doctorId);
    
    @Query("SELECT p FROM Patient p WHERE p.user.firstName LIKE %:search% OR p.user.lastName LIKE %:search% OR p.user.email LIKE %:search%")
    List<Patient> findBySearch(@Param("search") String search);
}
