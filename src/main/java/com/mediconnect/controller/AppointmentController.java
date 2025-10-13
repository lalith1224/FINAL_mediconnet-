package com.mediconnect.controller;

import com.mediconnect.dto.AppointmentBookingRequest;
import com.mediconnect.dto.AppointmentResponse;
import com.mediconnect.entity.Appointment;
import com.mediconnect.entity.Doctor;
import com.mediconnect.entity.Patient;
import com.mediconnect.entity.User;
import com.mediconnect.service.AppointmentService;
import com.mediconnect.service.AuthService;
import com.mediconnect.service.DoctorService;
import com.mediconnect.service.PatientService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Set;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    
    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private PatientService patientService;
    
    @Autowired
    private DoctorService doctorService;
    
    

    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(@Valid @RequestBody AppointmentBookingRequest request, HttpSession session) {
        try {
            // Get current user (patient)
            Optional<User> userOpt = authService.getCurrentUser(session);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("message", "Please login to book appointment"));
            }
            
            User user = userOpt.get();
            if (!user.getRole().equals(User.Role.PATIENT)) {
                return ResponseEntity.status(403).body(Map.of("message", "Only patients can book appointments"));
            }
            
            // Get patient profile
            Optional<Patient> patientOpt = patientService.findByUserId(user.getId());
            if (patientOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Patient profile not found"));
            }
            
            // Get doctor
            Optional<Doctor> doctorOpt = doctorService.findById(request.getDoctorId());
            if (doctorOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Doctor not found"));
            }
            
            // Check if doctor can accept more appointments on this date
            if (!appointmentService.canBookAppointment(request.getDoctorId(), request.getAppointmentDate().toLocalDate())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Doctor has reached daily appointment limit"));
            }
            
            // Create appointment
            Appointment appointment = new Appointment(
                patientOpt.get(),
                doctorOpt.get(),
                request.getAppointmentDate(),
                request.getAppointmentType()
            );
            appointment.setReason(request.getReason());
            appointment.setStatus(Appointment.AppointmentStatus.SCHEDULED);
            
            Appointment savedAppointment = appointmentService.createAppointment(appointment);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Appointment booked successfully");
            response.put("appointment", createAppointmentResponse(savedAppointment));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<?> cancelAppointment(@PathVariable UUID appointmentId, HttpSession session) {
        try {
            Optional<User> userOpt = authService.getCurrentUser(session);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("message", "Not authenticated"));
            }

            Optional<Appointment> appointmentOpt = appointmentService.findById(appointmentId);
            if (appointmentOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Appointment not found"));
            }

            Appointment appointment = appointmentOpt.get();
            User user = userOpt.get();

            boolean authorized = false;
            if (user.getRole().equals(User.Role.PATIENT)) {
                authorized = appointment.getPatient().getUser().getId().equals(user.getId());
            } else if (user.getRole().equals(User.Role.DOCTOR)) {
                authorized = appointment.getDoctor().getUser().getId().equals(user.getId());
            }

            if (!authorized) {
                return ResponseEntity.status(403).body(Map.of("message", "Not authorized to cancel this appointment"));
            }

            appointmentService.deleteAppointment(appointmentId);
            return ResponseEntity.ok(Map.of("message", "Appointment cancelled successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/doctor/booked")
    public ResponseEntity<?> getDoctorBookedAppointments(HttpSession session) {
        try {
            Optional<User> userOpt = authService.getCurrentUser(session);
            if (userOpt.isEmpty() || !userOpt.get().getRole().equals(User.Role.DOCTOR)) {
                return ResponseEntity.status(401).body(Map.of("message", "Not authenticated as doctor"));
            }

            Optional<Doctor> doctorOpt = doctorService.findByUserId(userOpt.get().getId());
            if (doctorOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Doctor profile not found"));
            }

            List<Appointment> appointments = appointmentService.findByDoctorId(doctorOpt.get().getId());
            List<AppointmentResponse> response = appointments.stream()
                .map(this::createAppointmentResponse)
                .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/my-appointments")
    public ResponseEntity<?> getMyAppointments(HttpSession session) {
        try {
            Optional<User> userOpt = authService.getCurrentUser(session);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("message", "Not authenticated"));
            }
            
            User user = userOpt.get();
            List<Appointment> appointments;
            
            if (user.getRole().equals(User.Role.PATIENT)) {
                Optional<Patient> patientOpt = patientService.findByUserId(user.getId());
                if (patientOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Patient profile not found"));
                }
                appointments = appointmentService.findByPatientId(patientOpt.get().getId());
            } else if (user.getRole().equals(User.Role.DOCTOR)) {
                Optional<Doctor> doctorOpt = doctorService.findByUserId(user.getId());
                if (doctorOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Doctor profile not found"));
                }
                appointments = appointmentService.findByDoctorId(doctorOpt.get().getId());
            } else {
                return ResponseEntity.status(403).body(Map.of("message", "Only patients and doctors can view appointments"));
            }
            
            List<AppointmentResponse> appointmentResponses = appointments.stream()
                .map(this::createAppointmentResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(appointmentResponses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/upcoming")
    public ResponseEntity<?> getUpcomingAppointments(HttpSession session) {
        try {
            Optional<User> userOpt = authService.getCurrentUser(session);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("message", "Not authenticated"));
            }
            
            User user = userOpt.get();
            List<Appointment> appointments;
            
            if (user.getRole().equals(User.Role.PATIENT)) {
                Optional<Patient> patientOpt = patientService.findByUserId(user.getId());
                if (patientOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Patient profile not found"));
                }
                appointments = appointmentService.findUpcomingByPatientId(patientOpt.get().getId());
            } else if (user.getRole().equals(User.Role.DOCTOR)) {
                Optional<Doctor> doctorOpt = doctorService.findByUserId(user.getId());
                if (doctorOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Doctor profile not found"));
                }
                appointments = appointmentService.findUpcomingByDoctorId(doctorOpt.get().getId());
            } else {
                return ResponseEntity.status(403).body(Map.of("message", "Only patients and doctors can view appointments"));
            }
            
            List<AppointmentResponse> appointmentResponses = appointments.stream()
                .map(this::createAppointmentResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(appointmentResponses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/doctors")
    public ResponseEntity<?> getAvailableDoctors() {
        try {
            List<Doctor> doctors = doctorService.getAllDoctors();
            
            List<Map<String, Object>> doctorList = doctors.stream()
                .map(doctor -> {
                    Map<String, Object> doctorInfo = new HashMap<>();
                    doctorInfo.put("id", doctor.getId().toString());
                    doctorInfo.put("name", "Dr. " + doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName());
                    doctorInfo.put("specialization", doctor.getSpecialization());
                    return doctorInfo;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(doctorList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/dashboard-stats")
    public ResponseEntity<?> getDashboardStats(HttpSession session) {
        try {
            Optional<User> userOpt = authService.getCurrentUser(session);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("message", "Not authenticated"));
            }
            
            User user = userOpt.get();
            Map<String, Object> stats = new HashMap<>();
            
            if (user.getRole().equals(User.Role.PATIENT)) {
                Optional<Patient> patientOpt = patientService.findByUserId(user.getId());
                if (patientOpt.isPresent()) {
                    List<Appointment> appointments = appointmentService.findByPatientId(patientOpt.get().getId());
                    stats.put("totalAppointments", appointments.size());
                    stats.put("upcomingAppointments", appointments.stream()
                        .filter(apt -> apt.getAppointmentDate().isAfter(LocalDateTime.now()))
                        .count());
                    stats.put("completedAppointments", appointments.stream()
                        .filter(apt -> apt.getStatus() == Appointment.AppointmentStatus.COMPLETED)
                        .count());
                }
            } else if (user.getRole().equals(User.Role.DOCTOR)) {
                Optional<Doctor> doctorOpt = doctorService.findByUserId(user.getId());
                if (doctorOpt.isPresent()) {
                    List<Appointment> appointments = appointmentService.findByDoctorId(doctorOpt.get().getId());
                    stats.put("totalAppointments", appointments.size());
                    stats.put("todayAppointments", appointments.stream()
                        .filter(apt -> apt.getAppointmentDate().toLocalDate().equals(LocalDate.now()))
                        .count());
                    
                    // Unique patient count
                    Set<UUID> uniquePatients = appointments.stream()
                        .map(appointment -> appointment.getPatient().getId())
                        .collect(Collectors.toSet());
                    stats.put("patientCount", uniquePatients.size());
                }
            }
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{appointmentId}/status")
    public ResponseEntity<?> updateAppointmentStatus(
            @PathVariable UUID appointmentId,
            @RequestBody Map<String, String> request) {
        try {
            Optional<Appointment> appointmentOpt = appointmentService.findById(appointmentId);
            if (appointmentOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Appointment not found"));
            }
            
            Appointment appointment = appointmentOpt.get();
            String newStatus = request.get("status");
            
            try {
                Appointment.AppointmentStatus status = Appointment.AppointmentStatus.valueOf(newStatus.toUpperCase());
                appointment.setStatus(status);
                
                Appointment updatedAppointment = appointmentService.updateAppointment(appointment);
                
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Appointment status updated successfully");
                response.put("appointment", createAppointmentResponse(updatedAppointment));
                
                return ResponseEntity.ok(response);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid status value"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/doctor-availability/{doctorId}")
    public ResponseEntity<?> checkDoctorAvailability(@PathVariable UUID doctorId, @RequestParam String date) {
        try {
            LocalDate appointmentDate = LocalDate.parse(date);
            boolean canBook = appointmentService.canBookAppointment(doctorId, appointmentDate);
            
            Map<String, Object> response = new HashMap<>();
            response.put("available", canBook);
            response.put("date", date);
            response.put("doctorId", doctorId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    private AppointmentResponse createAppointmentResponse(Appointment appointment) {
        AppointmentResponse response = new AppointmentResponse();
        response.setId(appointment.getId());
        response.setAppointmentDate(appointment.getAppointmentDate());
        response.setAppointmentType(appointment.getAppointmentType());
        response.setStatus(appointment.getStatus());
        response.setReason(appointment.getReason());
        response.setNotes(appointment.getNotes());
        response.setDiagnosis(appointment.getDiagnosis());
        response.setTreatmentPlan(appointment.getTreatmentPlan());
        
        // Patient info
        response.setPatientId(appointment.getPatient().getId());
        response.setPatientName(appointment.getPatient().getUser().getFirstName() + " " + 
                               appointment.getPatient().getUser().getLastName());
        response.setPatientEmail(appointment.getPatient().getUser().getEmail());
        response.setPatientPhone(appointment.getPatient().getPhone());
        
        // Doctor info
        response.setDoctorId(appointment.getDoctor().getId());
        response.setDoctorName("Dr. " + appointment.getDoctor().getUser().getFirstName() + " " + 
                              appointment.getDoctor().getUser().getLastName());
        response.setDoctorSpecialization(appointment.getDoctor().getSpecialization());
        
        return response;
    }
}
