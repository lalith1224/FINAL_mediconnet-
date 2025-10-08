package com.mediconnect.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Email
    @NotBlank
    @Column(unique = true, nullable = false)
    private String email;
    
    @NotBlank
    @Size(min = 1, max = 100)
    @Column(name = "first_name")
    private String firstName;
    
    @NotBlank
    @Size(min = 1, max = 100)
    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "profile_image_url")
    private String profileImageUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public User() {}
    
    public User(String email, String firstName, String lastName, Role role, String passwordHash) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.passwordHash = passwordHash;
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public enum Role {
        PATIENT, DOCTOR, PHARMACY
    }
}
