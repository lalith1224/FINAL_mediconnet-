package com.mediconnect.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "medications")
public class Medication {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;
    
    @Column(name = "medicine_name", nullable = false)
    private String medicineName;
    
    @Column(name = "generic_name")
    private String genericName;
    
    private String dosage;
    
    private String frequency;
    
    private Integer duration;
    
    @Column(name = "duration_unit")
    private String durationUnit; // days, weeks, months
    
    @Column(columnDefinition = "TEXT")
    private String instructions;
    
    // Constructors
    public Medication() {}
    
    public Medication(Prescription prescription, String medicineName, String dosage, String frequency) {
        this.prescription = prescription;
        this.medicineName = medicineName;
        this.dosage = dosage;
        this.frequency = frequency;
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public Prescription getPrescription() { return prescription; }
    public void setPrescription(Prescription prescription) { this.prescription = prescription; }
    
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
    
    public String getGenericName() { return genericName; }
    public void setGenericName(String genericName) { this.genericName = genericName; }
    
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    
    public String getDurationUnit() { return durationUnit; }
    public void setDurationUnit(String durationUnit) { this.durationUnit = durationUnit; }
    
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
}
