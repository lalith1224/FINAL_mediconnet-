package com.mediconnect.controller;

import com.mediconnect.dto.ChatRequest;
import com.mediconnect.dto.ChatResponse;
import com.mediconnect.entity.User;
import com.mediconnect.service.AuthService;
import com.mediconnect.service.ChatbotService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    @Autowired
    private AuthService authService;

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody ChatRequest chatRequest, HttpSession session) {
        try {
            // Verify user is authenticated
            Optional<User> userOpt = authService.getCurrentUser(session);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
            }

            User user = userOpt.get();
            
            // Set the user role in the chat request
            chatRequest.setUserRole(user.getRole().name());

            // Validate request
            if (chatRequest.getMessages() == null || chatRequest.getMessages().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Messages cannot be empty"));
            }

            // Process chat request
            ChatResponse response = chatbotService.chat(chatRequest);

            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(500).body(response);
            }

        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    ChatResponse.error("An error occurred: " + e.getMessage())
            );
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getStatus(HttpSession session) {
        Optional<User> userOpt = authService.getCurrentUser(session);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        return ResponseEntity.ok(Map.of(
                "status", "online",
                "userRole", userOpt.get().getRole().name(),
                "message", "Chatbot is ready to assist you"
        ));
    }
}
