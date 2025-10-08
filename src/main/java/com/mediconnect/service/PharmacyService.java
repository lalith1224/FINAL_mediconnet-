package com.mediconnect.service;

import com.mediconnect.entity.Pharmacy;
import com.mediconnect.repository.PharmacyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PharmacyService {
    
    @Autowired
    private PharmacyRepository pharmacyRepository;
    
    public Pharmacy createPharmacy(Pharmacy pharmacy) {
        return pharmacyRepository.save(pharmacy);
    }
    
    public Optional<Pharmacy> findById(UUID id) {
        return pharmacyRepository.findById(id);
    }
    
    public Optional<Pharmacy> findByUserId(UUID userId) {
        return pharmacyRepository.findByUserId(userId);
    }
    
    public Optional<Pharmacy> findByUserEmail(String email) {
        return pharmacyRepository.findByUserEmail(email);
    }
    
    public List<Pharmacy> searchPharmacies(String search) {
        return pharmacyRepository.findBySearch(search);
    }
    
    public List<Pharmacy> findByName(String name) {
        return pharmacyRepository.findByPharmacyNameContainingIgnoreCase(name);
    }
    
    public Pharmacy updatePharmacy(Pharmacy pharmacy) {
        return pharmacyRepository.save(pharmacy);
    }
    
    public void deletePharmacy(UUID id) {
        pharmacyRepository.deleteById(id);
    }
    
    public List<Pharmacy> getAllPharmacies() {
        return pharmacyRepository.findAll();
    }
}
