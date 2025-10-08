package com.mediconnect.controller;

import com.mediconnect.dto.LoginRequest;
import com.mediconnect.dto.RegisterRequest;
import com.mediconnect.entity.User;
import com.mediconnect.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        try {
            User user = authService.login(request.getEmail(), request.getPassword(), session);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("user", createUserResponse(user));
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = authService.register(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Registration successful");
            response.put("user", createUserResponse(user));
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        authService.logout(session);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout successful");
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        Optional<User> userOpt = authService.getCurrentUser(session);
        
        if (userOpt.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Not authenticated");
            return ResponseEntity.status(401).body(error);
        }
        
        return ResponseEntity.ok(createUserResponse(userOpt.get()));
    }
    
    private Map<String, Object> createUserResponse(User user) {
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", user.getId());
        userResponse.put("email", user.getEmail());
        userResponse.put("firstName", user.getFirstName());
        userResponse.put("lastName", user.getLastName());
        userResponse.put("role", user.getRole());
        userResponse.put("profileImageUrl", user.getProfileImageUrl());
        return userResponse;
    }
}
