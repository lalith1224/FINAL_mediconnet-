package com.mediconnect.controller;

import com.mediconnect.service.ChatbotService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {
    
    @Autowired
    private ChatbotService chatbotService;
    
    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody ChatRequest request, HttpSession session) {
        try {
            // Check if user is authenticated
            String userId = (String) session.getAttribute("userId");
            String userRole = (String) session.getAttribute("userRole");
            
            if (userId == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("error", "Please log in to use the AI assistant.");
                return ResponseEntity.status(401).body(error);
            }
            
            // Validate request
            if (request.getMessages() == null || request.getMessages().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("error", "No message provided.");
                return ResponseEntity.badRequest().body(error);
            }
            
            // Generate AI response
            String response = chatbotService.generateResponse(request.getMessages(), userId, userRole);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", response);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            System.err.println("Chatbot controller error: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "I'm having trouble processing your request right now. Please try again later.");
            
            return ResponseEntity.status(500).body(error);
        }
    }
    
    @PostMapping("/clear")
    public ResponseEntity<?> clearChat(HttpSession session) {
        try {
            String userId = (String) session.getAttribute("userId");
            if (userId != null) {
                chatbotService.clearChatHistory(userId);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Chat history cleared");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to clear chat history");
            
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/test")
    public ResponseEntity<?> testChatbot() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Chatbot service is running!");
        result.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(result);
    }
    
    // DTO for chat request
    public static class ChatRequest {
        private List<ChatMessage> messages;
        
        public List<ChatMessage> getMessages() {
            return messages;
        }
        
        public void setMessages(List<ChatMessage> messages) {
            this.messages = messages;
        }
    }
    
    public static class ChatMessage {
        private String role;
        private String content;
        
        public String getRole() {
            return role;
        }
        
        public void setRole(String role) {
            this.role = role;
        }
        
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
    }
}