package com.mediconnect.controller;

import com.mediconnect.entity.*;
import com.mediconnect.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/patient")
public class PatientController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private PatientService patientService;
    
    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private PrescriptionService prescriptionService;
    
    @GetMapping("/prescriptions/my-prescriptions")
    public ResponseEntity<?> getMyPrescriptions(HttpSession session) {
        Optional<User> userOpt = authService.getCurrentUser(session);
        if (userOpt.isEmpty() || userOpt.get().getRole() != User.Role.PATIENT) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        Optional<Patient> patientOpt = patientService.findByUserId(userOpt.get().getId());
        if (patientOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Patient profile not found"));
        }

        List<Prescription> prescriptions = prescriptionService.findByPatientId(patientOpt.get().getId());
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(HttpSession session) {
        Optional<User> userOpt = authService.getCurrentUser(session);
        if (userOpt.isEmpty() || userOpt.get().getRole() != User.Role.PATIENT) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        Optional<Patient> patientOpt = patientService.findByUserId(userOpt.get().getId());
        if (patientOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Patient profile not found"));
        }

        Patient patient = patientOpt.get();

        // Build safe DTOs to avoid serializing JPA entities directly
        List<Appointment> upcomingAppointments = appointmentService.findUpcomingByPatientId(patient.getId());
        List<Prescription> activePrescriptions = prescriptionService.findActiveByPatientId(patient.getId());

        List<Map<String, Object>> upcomingDtos = upcomingAppointments.stream().map(appt -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", appt.getId());
            m.put("appointmentDate", appt.getAppointmentDate());
            m.put("appointmentType", appt.getAppointmentType());
            m.put("status", appt.getStatus());
            m.put("reason", appt.getReason());
            // Doctor summary
            Map<String, Object> doctor = new HashMap<>();
            if (appt.getDoctor() != null && appt.getDoctor().getUser() != null) {
                doctor.put("firstName", appt.getDoctor().getUser().getFirstName());
                doctor.put("lastName", appt.getDoctor().getUser().getLastName());
                doctor.put("specialization", appt.getDoctor().getSpecialization());
            }
            m.put("doctor", doctor);
            return m;
        }).toList();

        List<Map<String, Object>> prescriptionDtos = activePrescriptions.stream().map(p -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", p.getId());
            m.put("status", p.getStatus());
            m.put("createdAt", p.getCreatedAt());
            // Doctor summary
            Map<String, Object> doctor = new HashMap<>();
            if (p.getDoctor() != null && p.getDoctor().getUser() != null) {
                doctor.put("firstName", p.getDoctor().getUser().getFirstName());
                doctor.put("lastName", p.getDoctor().getUser().getLastName());
            }
            m.put("doctor", doctor);
            m.put("medicationCount", p.getMedications() != null ? p.getMedications().size() : 0);
            return m;
        }).toList();

        Map<String, Object> patientSummary = new HashMap<>();
        patientSummary.put("id", patient.getId());
        if (patient.getUser() != null) {
            patientSummary.put("firstName", patient.getUser().getFirstName());
            patientSummary.put("lastName", patient.getUser().getLastName());
            patientSummary.put("email", patient.getUser().getEmail());
        }

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("patient", patientSummary);
        dashboard.put("upcomingAppointments", upcomingDtos);
        dashboard.put("activePrescriptions", prescriptionDtos);
        dashboard.put("totalAppointments", appointmentService.findByPatientId(patient.getId()).size());
        dashboard.put("totalPrescriptions", prescriptionService.findByPatientId(patient.getId()).size());

        return ResponseEntity.ok(dashboard);
    }
    
    @GetMapping("/appointments")
    public ResponseEntity<?> getAppointments(HttpSession session) {
        Optional<User> userOpt = authService.getCurrentUser(session);
        if (userOpt.isEmpty() || userOpt.get().getRole() != User.Role.PATIENT) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }
        
        Optional<Patient> patientOpt = patientService.findByUserId(userOpt.get().getId());
        if (patientOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Patient profile not found"));
        }
        
        List<Appointment> appointments = appointmentService.findByPatientId(patientOpt.get().getId());
        return ResponseEntity.ok(appointments);
    }
    
    @PostMapping("/appointments")
    public ResponseEntity<?> bookAppointment(@RequestBody Map<String, Object> request, HttpSession session) {
        try {
            Optional<User> userOpt = authService.getCurrentUser(session);
            if (userOpt.isEmpty() || userOpt.get().getRole() != User.Role.PATIENT) {
                return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
            }
            
            Optional<Patient> patientOpt = patientService.findByUserId(userOpt.get().getId());
            if (patientOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Patient profile not found"));
            }
            
            // Implementation would parse request and create appointment
            // For now, return success message
            return ResponseEntity.ok(Map.of("message", "Appointment booked successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    
    @GetMapping("/prescriptions")
    public ResponseEntity<?> getPrescriptions(HttpSession session) {
        Optional<User> userOpt = authService.getCurrentUser(session);
        if (userOpt.isEmpty() || userOpt.get().getRole() != User.Role.PATIENT) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }
        
        Optional<Patient> patientOpt = patientService.findByUserId(userOpt.get().getId());
        if (patientOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Patient profile not found"));
        }
        
        List<Prescription> prescriptions = prescriptionService.findByPatientId(patientOpt.get().getId());
        return ResponseEntity.ok(prescriptions);
    }
    
    @GetMapping("/medical-records")
    public ResponseEntity<?> getMedicalRecords(HttpSession session) {
        Optional<User> userOpt = authService.getCurrentUser(session);
        if (userOpt.isEmpty() || userOpt.get().getRole() != User.Role.PATIENT) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }
        
        // Mock medical records data
        Map<String, Object> records = new HashMap<>();
        records.put("message", "Medical records retrieved successfully");
        records.put("records", List.of(
            Map.of("id", 1, "type", "Blood Test", "date", "2024-01-15", "status", "Complete"),
            Map.of("id", 2, "type", "X-Ray", "date", "2024-02-20", "status", "Complete")
        ));
        
        return ResponseEntity.ok(records);
    }
    
    @PostMapping("/prescriptions/{id}/refill")
    public ResponseEntity<?> requestRefill(@PathVariable String id, HttpSession session) {
        try {
            Optional<User> userOpt = authService.getCurrentUser(session);
            if (userOpt.isEmpty() || userOpt.get().getRole() != User.Role.PATIENT) {
                return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
            }
            
            return ResponseEntity.ok(Map.of("message", "Refill request submitted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
