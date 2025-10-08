package com.mediconnect.service;

import com.mediconnect.entity.Doctor;
import com.mediconnect.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class DoctorService {
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    public Doctor createDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }
    
    public Optional<Doctor> findById(UUID id) {
        return doctorRepository.findById(id);
    }
    
    public Optional<Doctor> findByUserId(UUID userId) {
        return doctorRepository.findByUserId(userId);
    }
    
    public Optional<Doctor> findByUserEmail(String email) {
        return doctorRepository.findByUserEmail(email);
    }
    
    public List<Doctor> findBySpecialization(String specialization) {
        return doctorRepository.findBySpecialization(specialization);
    }
    
    public List<Doctor> searchDoctors(String search) {
        return doctorRepository.findBySearch(search);
    }
    
    public List<String> getAllSpecializations() {
        return doctorRepository.findAllSpecializations();
    }
    
    public Doctor updateDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }
    
    public void deleteDoctor(UUID id) {
        doctorRepository.deleteById(id);
    }
    
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }
}
