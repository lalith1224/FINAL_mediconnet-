package com.mediconnect.service;

import com.mediconnect.entity.Patient;
import com.mediconnect.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PatientService {
    
    @Autowired
    private PatientRepository patientRepository;
    
    public Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }
    
    public Optional<Patient> findById(UUID id) {
        return patientRepository.findById(id);
    }
    
    public Optional<Patient> findByUserId(UUID userId) {
        return patientRepository.findByUserId(userId);
    }
    
    public Optional<Patient> findByUserEmail(String email) {
        return patientRepository.findByUserEmail(email);
    }
    
    public List<Patient> findByDoctorId(UUID doctorId) {
        return patientRepository.findByDoctorId(doctorId);
    }
    
    public List<Patient> searchPatients(String search) {
        return patientRepository.findBySearch(search);
    }
    
    public Patient updatePatient(Patient patient) {
        return patientRepository.save(patient);
    }
    
    public void deletePatient(UUID id) {
        patientRepository.deleteById(id);
    }
    
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }
}
