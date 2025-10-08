package com.mediconnect.repository;

import com.mediconnect.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {
    
    List<InventoryItem> findByPharmacyId(UUID pharmacyId);
    
    @Query("SELECT i FROM InventoryItem i WHERE i.pharmacy.id = :pharmacyId AND i.currentStock <= i.minStockLevel")
    List<InventoryItem> findLowStockByPharmacyId(@Param("pharmacyId") UUID pharmacyId);
    
    @Query("SELECT i FROM InventoryItem i WHERE i.pharmacy.id = :pharmacyId AND i.medicineName LIKE %:search%")
    List<InventoryItem> findByPharmacyIdAndMedicineNameContaining(@Param("pharmacyId") UUID pharmacyId, @Param("search") String search);
    
    @Query("SELECT i FROM InventoryItem i WHERE i.medicineName LIKE %:medicineName%")
    List<InventoryItem> findByMedicineNameContaining(@Param("medicineName") String medicineName);
}
