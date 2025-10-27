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
import java.util.UUID;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private DoctorService doctorService;
    
    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private PrescriptionService prescriptionService;
    
    @Autowired
    private PatientService patientService;
    
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(HttpSession session) {
        Optional<User> userOpt = authService.getCurrentUser(session);
        if (userOpt.isEmpty() || userOpt.get().getRole() != User.Role.DOCTOR) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }
        
        Optional<Doctor> doctorOpt = doctorService.findByUserId(userOpt.get().getId());
        if (doctorOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Doctor profile not found"));
        }
        
        Doctor doctor = doctorOpt.get();
        List<Appointment> todayAppointments = appointmentService.findTodayAppointmentsByDoctorId(doctor.getId());
        List<Appointment> upcomingAppointments = appointmentService.findUpcomingByDoctorId(doctor.getId());
        List<Patient> patients = patientService.findByDoctorId(doctor.getId());
        
        // Get all appointments for statistics
        List<Appointment> allAppointments = appointmentService.findByDoctorId(doctor.getId());
        long completedAppointments = allAppointments.stream()
                .filter(a -> "COMPLETED".equals(a.getStatus()))
                .count();
        long pendingAppointments = allAppointments.stream()
                .filter(a -> "PENDING".equals(a.getStatus()) || "CONFIRMED".equals(a.getStatus()))
                .count();
        long cancelledAppointments = allAppointments.stream()
                .filter(a -> "CANCELLED".equals(a.getStatus()))
                .count();

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("doctor", doctor);
        dashboard.put("todayAppointments", todayAppointments);
        dashboard.put("upcomingAppointments", upcomingAppointments);
        dashboard.put("totalPatients", patients.size());
        dashboard.put("totalAppointments", allAppointments.size());

        // Add stats object that frontend expects
        Map<String, Object> stats = new HashMap<>();
        stats.put("todayPatients", todayAppointments.size());
        stats.put("completedAppointments", completedAppointments);
        stats.put("pendingAppointments", pendingAppointments);
        stats.put("cancelledAppointments", cancelledAppointments);
        stats.put("totalPrescriptions", prescriptionService.countByDoctorId(doctor.getId()));

        dashboard.put("stats", stats);
        
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/patients")
    public ResponseEntity<?> getPatients(HttpSession session) {
        Optional<User> userOpt = authService.getCurrentUser(session);
        if (userOpt.isEmpty() || userOpt.get().getRole() != User.Role.DOCTOR) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }
        
        Optional<Doctor> doctorOpt = doctorService.findByUserId(userOpt.get().getId());
        if (doctorOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Doctor profile not found"));
        }
        
        List<Patient> patients = patientService.findByDoctorId(doctorOpt.get().getId());
        return ResponseEntity.ok(patients);
    }
    
    @PostMapping("/prescriptions")
    public ResponseEntity<?> createPrescription(@RequestBody Map<String, Object> request, HttpSession session) {
        try {
            Optional<User> userOpt = authService.getCurrentUser(session);
            if (userOpt.isEmpty() || userOpt.get().getRole() != User.Role.DOCTOR) {
                return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
            }
            
            Optional<Doctor> doctorOpt = doctorService.findByUserId(userOpt.get().getId());
            if (doctorOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Doctor profile not found"));
            }
            
            // Implementation would parse request and create prescription
            return ResponseEntity.ok(Map.of("message", "Prescription created successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    
    @PutMapping("/appointment-limit")
    public ResponseEntity<?> updateAppointmentLimit(@RequestBody Map<String, Integer> request, HttpSession session) {
        try {
            Optional<User> userOpt = authService.getCurrentUser(session);
            if (userOpt.isEmpty() || userOpt.get().getRole() != User.Role.DOCTOR) {
                return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
            }
            
            Optional<Doctor> doctorOpt = doctorService.findByUserId(userOpt.get().getId());
            if (doctorOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Doctor profile not found"));
            }
            
            Doctor doctor = doctorOpt.get();
            doctor.setDailyAppointmentLimit(request.get("limit"));
            doctorService.updateDoctor(doctor);
            
            return ResponseEntity.ok(Map.of("message", "Appointment limit updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    
    @GetMapping("/{doctorId}/availability")
    public ResponseEntity<?> checkAvailability(@PathVariable String doctorId, @RequestParam String date) {
        try {
            UUID doctorUuid = UUID.fromString(doctorId);
            boolean available = appointmentService.canBookAppointment(doctorUuid, java.time.LocalDate.parse(date));
            
            Map<String, Object> response = new HashMap<>();
            response.put("available", available);
            response.put("date", date);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
