package com.mediconnect.service;

import com.mediconnect.entity.User;
import com.mediconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public User createUser(String email, String firstName, String lastName, String password, User.Role role) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("User with email " + email + " already exists");
        }
        
        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);
        
        return userRepository.save(user);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }
    
    public List<User> findByRole(User.Role role) {
        return userRepository.findByRole(role);
    }
    
    public List<User> searchByRoleAndQuery(User.Role role, String search) {
        return userRepository.findByRoleAndSearch(role, search);
    }
    
    public boolean validatePassword(User user, String password) {
        return passwordEncoder.matches(password, user.getPasswordHash());
    }
    
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }
    
    public long countByRole(User.Role role) {
        return userRepository.countByRole(role);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
