package com.mediconnect.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "pharmacies")
@EntityListeners(AuditingEntityListener.class)
public class Pharmacy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "pharmacy_name", nullable = false)
    private String pharmacyName;
    
    @Column(name = "license_number", nullable = false)
    private String licenseNumber;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;
    
    @Column(nullable = false)
    private String phone;
    
    @ElementCollection
    @CollectionTable(name = "pharmacy_operating_hours", joinColumns = @JoinColumn(name = "pharmacy_id"))
    @MapKeyColumn(name = "day_of_week")
    @Column(name = "hours")
    private Map<String, String> operatingHours;
    
    @Column(name = "services_offered", columnDefinition = "TEXT")
    private String servicesOffered;
    
    @OneToMany(mappedBy = "pharmacy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InventoryItem> inventory;
    
    @OneToMany(mappedBy = "pharmacy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Prescription> prescriptions;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public Pharmacy() {}
    
    public Pharmacy(User user, String pharmacyName, String licenseNumber, String address, String phone) {
        this.user = user;
        this.pharmacyName = pharmacyName;
        this.licenseNumber = licenseNumber;
        this.address = address;
        this.phone = phone;
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getPharmacyName() { return pharmacyName; }
    public void setPharmacyName(String pharmacyName) { this.pharmacyName = pharmacyName; }
    
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public Map<String, String> getOperatingHours() { return operatingHours; }
    public void setOperatingHours(Map<String, String> operatingHours) { this.operatingHours = operatingHours; }
    
    public String getServicesOffered() { return servicesOffered; }
    public void setServicesOffered(String servicesOffered) { this.servicesOffered = servicesOffered; }
    
    public List<InventoryItem> getInventory() { return inventory; }
    public void setInventory(List<InventoryItem> inventory) { this.inventory = inventory; }
    
    public List<Prescription> getPrescriptions() { return prescriptions; }
    public void setPrescriptions(List<Prescription> prescriptions) { this.prescriptions = prescriptions; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
