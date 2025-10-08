package com.mediconnect.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "prescriptions")
@EntityListeners(AuditingEntityListener.class)
public class Prescription {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;
    
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;
    
    @ManyToOne
    @JoinColumn(name = "pharmacy_id")
    private Pharmacy pharmacy;
    
    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Medication> medications;
    
    @Column(columnDefinition = "TEXT")
    private String instructions;
    
    @Enumerated(EnumType.STRING)
    private PrescriptionStatus status = PrescriptionStatus.PENDING;
    
    @Column(name = "valid_until")
    private LocalDateTime validUntil;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public Prescription() {}
    
    public Prescription(Patient patient, Doctor doctor) {
        this.patient = patient;
        this.doctor = doctor;
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public Appointment getAppointment() { return appointment; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }
    
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
    
    public Pharmacy getPharmacy() { return pharmacy; }
    public void setPharmacy(Pharmacy pharmacy) { this.pharmacy = pharmacy; }
    
    public List<Medication> getMedications() { return medications; }
    public void setMedications(List<Medication> medications) { this.medications = medications; }
    
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    
    public PrescriptionStatus getStatus() { return status; }
    public void setStatus(PrescriptionStatus status) { this.status = status; }
    
    public LocalDateTime getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDateTime validUntil) { this.validUntil = validUntil; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public enum PrescriptionStatus {
        PENDING, APPROVED, DISPENSED, COMPLETED
    }
}
