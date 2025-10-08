package com.mediconnect.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "appointments")
@EntityListeners(AuditingEntityListener.class)
public class Appointment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;
    
    @Column(name = "appointment_date", nullable = false)
    private LocalDateTime appointmentDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_type", nullable = false)
    private AppointmentType appointmentType;
    
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;
    
    @Column(columnDefinition = "TEXT")
    private String reason;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(columnDefinition = "TEXT")
    private String diagnosis;
    
    @Column(name = "treatment_plan", columnDefinition = "TEXT")
    private String treatmentPlan;
    
    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Prescription> prescriptions;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public Appointment() {}
    
    public Appointment(Patient patient, Doctor doctor, LocalDateTime appointmentDate, AppointmentType appointmentType) {
        this.patient = patient;
        this.doctor = doctor;
        this.appointmentDate = appointmentDate;
        this.appointmentType = appointmentType;
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
    
    public LocalDateTime getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDateTime appointmentDate) { this.appointmentDate = appointmentDate; }
    
    public AppointmentType getAppointmentType() { return appointmentType; }
    public void setAppointmentType(AppointmentType appointmentType) { this.appointmentType = appointmentType; }
    
    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    
    public String getTreatmentPlan() { return treatmentPlan; }
    public void setTreatmentPlan(String treatmentPlan) { this.treatmentPlan = treatmentPlan; }
    
    public List<Prescription> getPrescriptions() { return prescriptions; }
    public void setPrescriptions(List<Prescription> prescriptions) { this.prescriptions = prescriptions; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public enum AppointmentType {
        VIDEO, IN_PERSON
    }
    
    public enum AppointmentStatus {
        SCHEDULED, CONFIRMED, COMPLETED, CANCELLED
    }
}
