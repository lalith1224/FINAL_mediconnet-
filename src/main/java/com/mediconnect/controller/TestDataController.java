package com.mediconnect.controller;

import com.mediconnect.entity.Doctor;
import com.mediconnect.entity.Patient;
import com.mediconnect.entity.User;
import com.mediconnect.service.AuthService;
import com.mediconnect.service.DoctorService;
import com.mediconnect.service.PatientService;
import com.mediconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestDataController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private DoctorService doctorService;
    
    @Autowired
    private PatientService patientService;
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/create-test-doctors")
    public ResponseEntity<?> createTestDoctors() {
        try {
            // Create test doctors
            createTestDoctor("John", "Smith", "doctor1@test.com", "Cardiology", "MD123456", 10);
            createTestDoctor("Sarah", "Johnson", "doctor2@test.com", "Pediatrics", "MD789012", 8);
            createTestDoctor("Michael", "Brown", "doctor3@test.com", "Dermatology", "MD345678", 12);
            
            // Create test patient
            createTestPatient("Alice", "Wilson", "patient@test.com", LocalDate.of(1990, 5, 15), "Female");
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Test data created successfully");
            response.put("doctors", 3);
            response.put("patients", 1);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error creating test data: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    private void createTestDoctor(String firstName, String lastName, String email, 
                                 String specialization, String licenseNumber, int experience) {
        try {
            // Check if user already exists
            if (userService.findByEmail(email).isPresent()) {
                return; // Skip if already exists
            }
            
            // Create user using the correct method signature
            User user = userService.createUser(email, firstName, lastName, "password", User.Role.DOCTOR);
            
            // Create doctor
            Doctor doctor = new Doctor();
            doctor.setUser(user);
            doctor.setLicenseNumber(licenseNumber);
            doctor.setSpecialization(specialization);
            doctor.setExperience(experience);
            doctorService.createDoctor(doctor);
            
        } catch (Exception e) {
            System.err.println("Error creating doctor " + email + ": " + e.getMessage());
        }
    }
    
    private void createTestPatient(String firstName, String lastName, String email, 
                                  LocalDate dateOfBirth, String gender) {
        try {
            // Check if user already exists
            if (userService.findByEmail(email).isPresent()) {
                return; // Skip if already exists
            }
            
            // Create user using the correct method signature
            User user = userService.createUser(email, firstName, lastName, "password", User.Role.PATIENT);
            
            // Create patient
            Patient patient = new Patient();
            patient.setUser(user);
            patient.setDateOfBirth(dateOfBirth);
            patient.setGender(gender);
            patient.setPhone("+1234567890");
            patientService.createPatient(patient);
            
        } catch (Exception e) {
            System.err.println("Error creating patient " + email + ": " + e.getMessage());
        }
    }
}