package com.mediconnect.repository;

import com.mediconnect.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {
    
    List<Prescription> findByPatientId(UUID patientId);
    
    List<Prescription> findByDoctorId(UUID doctorId);
    
    @Query("SELECT COUNT(p) FROM Prescription p WHERE p.doctor.id = :doctorId")
    long countByDoctorId(@Param("doctorId") UUID doctorId);
    
    List<Prescription> findByPharmacyId(UUID pharmacyId);
    
    List<Prescription> findByStatus(Prescription.PrescriptionStatus status);
    
    @Query("SELECT p FROM Prescription p WHERE p.pharmacy.id = :pharmacyId AND p.status = :status")
    List<Prescription> findByPharmacyIdAndStatus(@Param("pharmacyId") UUID pharmacyId, @Param("status") Prescription.PrescriptionStatus status);
    
    @Query("SELECT p FROM Prescription p WHERE p.patient.id = :patientId AND p.status IN :statuses ORDER BY p.createdAt DESC")
    List<Prescription> findByPatientIdAndStatusIn(@Param("patientId") UUID patientId, @Param("statuses") List<Prescription.PrescriptionStatus> statuses);
}
