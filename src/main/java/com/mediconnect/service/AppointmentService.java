package com.mediconnect.service;

import com.mediconnect.entity.Appointment;
import com.mediconnect.entity.Doctor;
import com.mediconnect.entity.Patient;
import com.mediconnect.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AppointmentService {
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private DoctorService doctorService;
    
    public Appointment createAppointment(Appointment appointment) {
        // Check doctor's daily appointment limit
        Doctor doctor = appointment.getDoctor();
        LocalDate appointmentDate = appointment.getAppointmentDate().toLocalDate();
        
        long todayAppointments = appointmentRepository.countByDoctorIdAndDate(
            doctor.getId(), appointmentDate);
        
        if (todayAppointments >= doctor.getDailyAppointmentLimit()) {
            throw new RuntimeException("Doctor has reached daily appointment limit");
        }
        
        return appointmentRepository.save(appointment);
    }
    
    public Optional<Appointment> findById(UUID id) {
        return appointmentRepository.findById(id);
    }
    
    public List<Appointment> findByPatientId(UUID patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }
    
    public List<Appointment> findByDoctorId(UUID doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }
    
    public List<Appointment> findUpcomingByPatientId(UUID patientId) {
        return appointmentRepository.findUpcomingByPatientId(patientId, LocalDateTime.now());
    }
    
    public List<Appointment> findUpcomingByDoctorId(UUID doctorId) {
        return appointmentRepository.findUpcomingByDoctorId(doctorId, LocalDateTime.now());
    }
    
    public List<Appointment> findTodayAppointmentsByDoctorId(UUID doctorId) {
        return appointmentRepository.findByDoctorIdAndDate(doctorId, LocalDate.now());
    }
    
    public List<Appointment> findByStatus(Appointment.AppointmentStatus status) {
        return appointmentRepository.findByStatus(status);
    }
    
    public Appointment updateAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }
    
    public void deleteAppointment(UUID id) {
        appointmentRepository.deleteById(id);
    }
    
    public boolean canBookAppointment(UUID doctorId, LocalDate date) {
        Optional<Doctor> doctorOpt = doctorService.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            return false;
        }
        
        Doctor doctor = doctorOpt.get();
        long todayAppointments = appointmentRepository.countByDoctorIdAndDate(doctorId, date);
        
        return todayAppointments < doctor.getDailyAppointmentLimit();
    }
    
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }
}
