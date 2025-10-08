package com.mediconnect.repository;

import com.mediconnect.entity.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PharmacyRepository extends JpaRepository<Pharmacy, UUID> {
    
    Optional<Pharmacy> findByUserId(UUID userId);
    
    @Query("SELECT p FROM Pharmacy p WHERE p.user.email = :email")
    Optional<Pharmacy> findByUserEmail(@Param("email") String email);
    
    @Query("SELECT p FROM Pharmacy p WHERE p.pharmacyName LIKE %:search% OR p.address LIKE %:search%")
    List<Pharmacy> findBySearch(@Param("search") String search);
    
    List<Pharmacy> findByPharmacyNameContainingIgnoreCase(String name);
}
