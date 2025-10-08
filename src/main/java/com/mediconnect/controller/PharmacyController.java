package com.mediconnect.controller;

import com.mediconnect.entity.*;
import com.mediconnect.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/pharmacy")
public class PharmacyController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private PharmacyService pharmacyService;
    
    @Autowired
    private InventoryService inventoryService;
    
    @Autowired
    private PrescriptionService prescriptionService;
    
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(HttpSession session) {
        Optional<User> userOpt = authService.getCurrentUser(session);
        if (userOpt.isEmpty() || userOpt.get().getRole() != User.Role.PHARMACY) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }
        
        Optional<Pharmacy> pharmacyOpt = pharmacyService.findByUserId(userOpt.get().getId());
        if (pharmacyOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Pharmacy profile not found"));
        }
        
        Pharmacy pharmacy = pharmacyOpt.get();
        List<Prescription> pendingPrescriptions = prescriptionService.findByPharmacyIdAndStatus(
            pharmacy.getId(), Prescription.PrescriptionStatus.PENDING);
        List<InventoryItem> inventory = inventoryService.findByPharmacyId(pharmacy.getId());
        List<InventoryItem> lowStockItems = inventoryService.findLowStockByPharmacyId(pharmacy.getId());
        
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("pharmacy", pharmacy);
        dashboard.put("pendingPrescriptions", pendingPrescriptions);
        dashboard.put("totalInventoryItems", inventory.size());
        dashboard.put("lowStockItems", lowStockItems);
        dashboard.put("totalPrescriptions", prescriptionService.findByPharmacyId(pharmacy.getId()).size());
        
        return ResponseEntity.ok(dashboard);
    }
    
    @PostMapping("/inventory")
    public ResponseEntity<?> addInventoryItem(@RequestBody Map<String, Object> request, HttpSession session) {
        try {
            Optional<User> userOpt = authService.getCurrentUser(session);
            if (userOpt.isEmpty() || userOpt.get().getRole() != User.Role.PHARMACY) {
                return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
            }
            
            Optional<Pharmacy> pharmacyOpt = pharmacyService.findByUserId(userOpt.get().getId());
            if (pharmacyOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Pharmacy profile not found"));
            }
            
            // Implementation would parse request and create inventory item
            return ResponseEntity.ok(Map.of("message", "Inventory item added successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    
    @PutMapping("/prescriptions/{id}/status")
    public ResponseEntity<?> updatePrescriptionStatus(@PathVariable String id, 
                                                     @RequestBody Map<String, String> request, 
                                                     HttpSession session) {
        try {
            Optional<User> userOpt = authService.getCurrentUser(session);
            if (userOpt.isEmpty() || userOpt.get().getRole() != User.Role.PHARMACY) {
                return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
            }
            
            UUID prescriptionId = UUID.fromString(id);
            Prescription.PrescriptionStatus status = Prescription.PrescriptionStatus.valueOf(request.get("status"));
            
            prescriptionService.updatePrescriptionStatus(prescriptionId, status);
            
            return ResponseEntity.ok(Map.of("message", "Prescription status updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    
    @PostMapping("/prescriptions")
    public ResponseEntity<?> createPrescription(@RequestBody Map<String, Object> request, HttpSession session) {
        try {
            Optional<User> userOpt = authService.getCurrentUser(session);
            if (userOpt.isEmpty() || userOpt.get().getRole() != User.Role.PHARMACY) {
                return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
            }
            
            // Implementation would parse request and create prescription on behalf of doctor
            return ResponseEntity.ok(Map.of("message", "Prescription created successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
