package com.mediconnect.service;

import com.mediconnect.entity.Doctor;
import com.mediconnect.entity.Patient;
import com.mediconnect.entity.Pharmacy;
import com.mediconnect.entity.User;
import com.mediconnect.dto.RegisterRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AuthService {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PatientService patientService;
    
    @Autowired
    private DoctorService doctorService;
    
    @Autowired
    private PharmacyService pharmacyService;
    
    public User login(String email, String password, HttpSession session) {
        Optional<User> userOpt = userService.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid email or password");
        }
        
        User user = userOpt.get();
        if (!userService.validatePassword(user, password)) {
            throw new RuntimeException("Invalid email or password");
        }
        
        session.setAttribute("userId", user.getId().toString());
        session.setAttribute("userRole", user.getRole().toString());
        
        return user;
    }
    
    public User register(RegisterRequest request) {
        User user = userService.createUser(
            request.getEmail(),
            request.getFirstName(),
            request.getLastName(),
            request.getPassword(),
            request.getRole()
        );
        
        // Create role-specific profile
        switch (request.getRole()) {
            case PATIENT:
                Patient patient = new Patient(user);
                patient.setDateOfBirth(request.getDateOfBirth());
                patient.setGender(request.getGender());
                patient.setPhone(request.getPhone());
                patientService.createPatient(patient);
                break;
                
            case DOCTOR:
                // Provide default values for required fields if not provided
                String docLicense = request.getLicenseNumber() != null ? 
                    request.getLicenseNumber() : "DOC_" + System.currentTimeMillis();
                String specialization = request.getSpecialization() != null ? 
                    request.getSpecialization() : "General Practice";
                Doctor doctor = new Doctor(user, docLicense, specialization);
                doctor.setExperience(request.getExperience());
                doctorService.createDoctor(doctor);
                break;
                
            case PHARMACY:
                // For pharmacy, use a default license number if not provided
                String licenseNumber = request.getLicenseNumber() != null ? 
                    request.getLicenseNumber() : "TEMP_" + System.currentTimeMillis();
                // Provide default values for required pharmacy fields
                String pharmName = request.getPharmacyName() != null ? 
                    request.getPharmacyName() : "Pharmacy " + System.currentTimeMillis();
                String pharmAddress = request.getAddress() != null ? 
                    request.getAddress() : "Address not provided";
                String pharmPhone = request.getPhone() != null ? 
                    request.getPhone() : "000-000-0000";
                Pharmacy pharmacy = new Pharmacy(user, pharmName, 
                    licenseNumber, pharmAddress, pharmPhone);
                pharmacyService.createPharmacy(pharmacy);
                break;
        }
        
        return user;
    }
    
    public void logout(HttpSession session) {
        session.invalidate();
    }
    
    public Optional<User> getCurrentUser(HttpSession session) {
        String userIdStr = (String) session.getAttribute("userId");
        if (userIdStr == null) {
            return Optional.empty();
        }
        
        try {
            return userService.findById(java.util.UUID.fromString(userIdStr));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
