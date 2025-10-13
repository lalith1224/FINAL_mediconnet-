package com.mediconnect.service;

import com.mediconnect.entity.Prescription;
import com.mediconnect.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PrescriptionService {
    
    @Autowired
    private PrescriptionRepository prescriptionRepository;
    
    public Prescription createPrescription(Prescription prescription) {
        return prescriptionRepository.save(prescription);
    }
    
    public Optional<Prescription> findById(UUID id) {
        return prescriptionRepository.findById(id);
    }
    
    public List<Prescription> findByPatientId(UUID patientId) {
        return prescriptionRepository.findByPatientId(patientId);
    }
    
    public List<Prescription> findByDoctorId(UUID doctorId) {
        return prescriptionRepository.findByDoctorId(doctorId);
    }
    
    public long countByDoctorId(UUID doctorId) {
        return prescriptionRepository.countByDoctorId(doctorId);
    }
    
    public List<Prescription> findByPharmacyId(UUID pharmacyId) {
        return prescriptionRepository.findByPharmacyId(pharmacyId);
    }
    
    public List<Prescription> findByStatus(Prescription.PrescriptionStatus status) {
        return prescriptionRepository.findByStatus(status);
    }
    
    public List<Prescription> findByPharmacyIdAndStatus(UUID pharmacyId, Prescription.PrescriptionStatus status) {
        return prescriptionRepository.findByPharmacyIdAndStatus(pharmacyId, status);
    }
    
    public List<Prescription> findActiveByPatientId(UUID patientId) {
        List<Prescription.PrescriptionStatus> activeStatuses = List.of(
            Prescription.PrescriptionStatus.PENDING,
            Prescription.PrescriptionStatus.APPROVED,
            Prescription.PrescriptionStatus.DISPENSED
        );
        return prescriptionRepository.findByPatientIdAndStatusIn(patientId, activeStatuses);
    }
    
    public Prescription updatePrescription(Prescription prescription) {
        return prescriptionRepository.save(prescription);
    }
    
    public Prescription updatePrescriptionStatus(UUID prescriptionId, Prescription.PrescriptionStatus status) {
        Optional<Prescription> prescriptionOpt = prescriptionRepository.findById(prescriptionId);
        if (prescriptionOpt.isEmpty()) {
            throw new RuntimeException("Prescription not found");
        }
        
        Prescription prescription = prescriptionOpt.get();
        prescription.setStatus(status);
        return prescriptionRepository.save(prescription);
    }
    
    public void deletePrescription(UUID id) {
        prescriptionRepository.deleteById(id);
    }
    
    public List<Prescription> getAllPrescriptions() {
        return prescriptionRepository.findAll();
    }
}
