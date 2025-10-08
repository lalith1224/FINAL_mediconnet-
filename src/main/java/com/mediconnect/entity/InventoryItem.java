package com.mediconnect.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory")
@EntityListeners(AuditingEntityListener.class)
public class InventoryItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "pharmacy_id", nullable = false)
    private Pharmacy pharmacy;
    
    @Column(name = "medicine_name", nullable = false)
    private String medicineName;
    
    @Column(name = "generic_name")
    private String genericName;
    
    private String manufacturer;
    
    private String dosage;
    
    private String form; // tablet, capsule, syrup, etc.
    
    @Column(name = "current_stock")
    private Integer currentStock = 0;
    
    @Column(name = "min_stock_level")
    private Integer minStockLevel = 10;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "expiry_date")
    private LocalDate expiryDate;
    
    @Column(name = "batch_number")
    private String batchNumber;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public InventoryItem() {}
    
    public InventoryItem(Pharmacy pharmacy, String medicineName, Integer currentStock, BigDecimal price) {
        this.pharmacy = pharmacy;
        this.medicineName = medicineName;
        this.currentStock = currentStock;
        this.price = price;
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public Pharmacy getPharmacy() { return pharmacy; }
    public void setPharmacy(Pharmacy pharmacy) { this.pharmacy = pharmacy; }
    
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
    
    public String getGenericName() { return genericName; }
    public void setGenericName(String genericName) { this.genericName = genericName; }
    
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    
    public String getForm() { return form; }
    public void setForm(String form) { this.form = form; }
    
    public Integer getCurrentStock() { return currentStock; }
    public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }
    
    public Integer getMinStockLevel() { return minStockLevel; }
    public void setMinStockLevel(Integer minStockLevel) { this.minStockLevel = minStockLevel; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    
    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public boolean isLowStock() {
        return currentStock <= minStockLevel;
    }
    
    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }
}
