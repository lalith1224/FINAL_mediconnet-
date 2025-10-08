package com.mediconnect.dto;

import com.mediconnect.entity.Appointment;

import java.time.LocalDateTime;
import java.util.UUID;

public class AppointmentResponse {
    
    private UUID id;
    private LocalDateTime appointmentDate;
    private Appointment.AppointmentType appointmentType;
    private Appointment.AppointmentStatus status;
    private String reason;
    private String notes;
    private String diagnosis;
    private String treatmentPlan;
    
    // Patient info
    private UUID patientId;
    private String patientName;
    private String patientEmail;
    private String patientPhone;
    
    // Doctor info
    private UUID doctorId;
    private String doctorName;
    private String doctorSpecialization;
    
    // Constructors
    public AppointmentResponse() {}
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public LocalDateTime getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDateTime appointmentDate) { this.appointmentDate = appointmentDate; }
    
    public Appointment.AppointmentType getAppointmentType() { return appointmentType; }
    public void setAppointmentType(Appointment.AppointmentType appointmentType) { this.appointmentType = appointmentType; }
    
    public Appointment.AppointmentStatus getStatus() { return status; }
    public void setStatus(Appointment.AppointmentStatus status) { this.status = status; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    
    public String getTreatmentPlan() { return treatmentPlan; }
    public void setTreatmentPlan(String treatmentPlan) { this.treatmentPlan = treatmentPlan; }
    
    public UUID getPatientId() { return patientId; }
    public void setPatientId(UUID patientId) { this.patientId = patientId; }
    
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    
    public String getPatientEmail() { return patientEmail; }
    public void setPatientEmail(String patientEmail) { this.patientEmail = patientEmail; }
    
    public String getPatientPhone() { return patientPhone; }
    public void setPatientPhone(String patientPhone) { this.patientPhone = patientPhone; }
    
    public UUID getDoctorId() { return doctorId; }
    public void setDoctorId(UUID doctorId) { this.doctorId = doctorId; }
    
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    
    public String getDoctorSpecialization() { return doctorSpecialization; }
    public void setDoctorSpecialization(String doctorSpecialization) { this.doctorSpecialization = doctorSpecialization; }
}
