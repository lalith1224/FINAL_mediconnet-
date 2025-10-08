package com.mediconnect.repository;

import com.mediconnect.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
    
    Optional<Doctor> findByUserId(UUID userId);
    
    @Query("SELECT d FROM Doctor d WHERE d.user.email = :email")
    Optional<Doctor> findByUserEmail(@Param("email") String email);
    
    List<Doctor> findBySpecialization(String specialization);
    
    @Query("SELECT d FROM Doctor d WHERE d.specialization LIKE %:search% OR d.user.firstName LIKE %:search% OR d.user.lastName LIKE %:search%")
    List<Doctor> findBySearch(@Param("search") String search);
    
    @Query("SELECT DISTINCT d.specialization FROM Doctor d ORDER BY d.specialization")
    List<String> findAllSpecializations();
}
