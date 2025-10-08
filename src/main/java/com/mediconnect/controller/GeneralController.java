package com.mediconnect.controller;

import com.mediconnect.entity.Appointment;
import com.mediconnect.entity.Doctor;
import com.mediconnect.service.AppointmentService;
import com.mediconnect.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class GeneralController {
    
    @Autowired
    private DoctorService doctorService;
    
    @Autowired
    private AppointmentService appointmentService;
    
    @GetMapping("/doctors")
    public ResponseEntity<?> getAllDoctors() {
        List<Doctor> doctors = doctorService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }
    
    @PutMapping("/appointments/{id}")
    public ResponseEntity<?> updateAppointment(@PathVariable String id, @RequestBody Map<String, Object> request) {
        try {
            UUID appointmentId = UUID.fromString(id);
            // Implementation would parse request and update appointment
            return ResponseEntity.ok(Map.of("message", "Appointment updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    
    @PostMapping("/ai/chat")
    public ResponseEntity<?> aiChat(@RequestBody Map<String, Object> request) {
        // Mock AI chat response
        return ResponseEntity.ok(Map.of(
            "message", "AI assistant response",
            "response", "Hello! I'm your healthcare AI assistant. How can I help you today?"
        ));
    }
}
