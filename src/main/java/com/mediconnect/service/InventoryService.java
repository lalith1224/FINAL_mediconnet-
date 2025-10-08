package com.mediconnect.service;

import com.mediconnect.entity.InventoryItem;
import com.mediconnect.repository.InventoryItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class InventoryService {
    
    @Autowired
    private InventoryItemRepository inventoryItemRepository;
    
    public InventoryItem createInventoryItem(InventoryItem inventoryItem) {
        return inventoryItemRepository.save(inventoryItem);
    }
    
    public Optional<InventoryItem> findById(UUID id) {
        return inventoryItemRepository.findById(id);
    }
    
    public List<InventoryItem> findByPharmacyId(UUID pharmacyId) {
        return inventoryItemRepository.findByPharmacyId(pharmacyId);
    }
    
    public List<InventoryItem> findLowStockByPharmacyId(UUID pharmacyId) {
        return inventoryItemRepository.findLowStockByPharmacyId(pharmacyId);
    }
    
    public List<InventoryItem> searchByPharmacyIdAndMedicine(UUID pharmacyId, String search) {
        return inventoryItemRepository.findByPharmacyIdAndMedicineNameContaining(pharmacyId, search);
    }
    
    public List<InventoryItem> searchByMedicineName(String medicineName) {
        return inventoryItemRepository.findByMedicineNameContaining(medicineName);
    }
    
    public InventoryItem updateInventoryItem(InventoryItem inventoryItem) {
        return inventoryItemRepository.save(inventoryItem);
    }
    
    public void deleteInventoryItem(UUID id) {
        inventoryItemRepository.deleteById(id);
    }
    
    public List<InventoryItem> getAllInventoryItems() {
        return inventoryItemRepository.findAll();
    }
}
