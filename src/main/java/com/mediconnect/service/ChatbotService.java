package com.mediconnect.service;

import com.mediconnect.controller.ChatbotController.ChatMessage;
import com.mediconnect.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ChatbotService {
    
    @Value("${openrouter.api.key}")
    private String apiKey;
    
    @Value("${openrouter.api.url}")
    private String apiUrl;
    
    @Value("${openrouter.model}")
    private String model;
    
    @Autowired
    private UserService userService;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    // Store chat histories in memory (in production, use Redis or database)
    private final Map<String, List<ChatMessage>> chatHistories = new HashMap<>();
    
    public String generateResponse(List<ChatMessage> messages, String userId, String userRole) {
        try {
            // Get user context
            String userContext = getUserContext(userId, userRole);
            
            // Prepare messages for AI
            List<Map<String, String>> aiMessages = new ArrayList<>();
            
            // Add system message with context
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", getSystemPrompt() + "\n\nUser Context: " + userContext);
            aiMessages.add(systemMessage);
            
            // Add conversation history
            for (ChatMessage msg : messages) {
                Map<String, String> aiMessage = new HashMap<>();
                aiMessage.put("role", msg.getRole());
                aiMessage.put("content", msg.getContent());
                aiMessages.add(aiMessage);
            }
            
            // Prepare request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", aiMessages);
            requestBody.put("max_tokens", 500);
            requestBody.put("temperature", 0.7);
            
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            headers.set("HTTP-Referer", "https://mediconnect.local");
            headers.set("X-Title", "MediConnect AI Assistant");
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            // Make API call
            ResponseEntity<Map> response = restTemplate.exchange(
                apiUrl, 
                HttpMethod.POST, 
                entity, 
                Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> firstChoice = choices.get(0);
                    Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                    String content = (String) message.get("content");
                    
                    // Store in chat history
                    if (userId != null) {
                        storeChatHistory(userId, messages);
                    }
                    
                    return content != null ? content.trim() : "I'm sorry, I couldn't generate a response.";
                }
            }
            
            return "I'm having trouble connecting to the AI service. Please try again.";
            
        } catch (Exception e) {
            System.err.println("Chatbot error: " + e.getMessage());
            e.printStackTrace();
            return "I'm experiencing technical difficulties. Please try again later.";
        }
    }
    
    private String getUserContext(String userId, String userRole) {
        if (userId == null || userRole == null) {
            return "Anonymous user";
        }
        
        try {
            Optional<User> userOpt = userService.findById(UUID.fromString(userId));
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                return String.format("User: %s %s, Role: %s", 
                    user.getFirstName(), user.getLastName(), userRole);
            }
        } catch (Exception e) {
            System.err.println("Error getting user context: " + e.getMessage());
        }
        
        return "User role: " + userRole;
    }
    
    private String getSystemPrompt() {
        return """
            You are MediConnect AI Assistant, a helpful healthcare management assistant for the MediConnect platform.
            
            Your role is to help users with:
            - General healthcare information and guidance
            - Navigating the MediConnect platform features
            - Appointment scheduling assistance
            - Prescription management help
            - General medical questions (but always recommend consulting healthcare professionals)
            - Platform troubleshooting and support
            
            Guidelines:
            - Be professional, empathetic, and helpful
            - Always recommend consulting healthcare professionals for medical advice
            - Provide clear, concise responses
            - If you don't know something, admit it and suggest alternatives
            - Focus on being helpful within the healthcare management context
            - Never provide specific medical diagnoses or treatment recommendations
            - Keep responses conversational but informative
            
            Remember: You are an assistant for a healthcare management platform, not a replacement for medical professionals.
            """;
    }
    
    private void storeChatHistory(String userId, List<ChatMessage> messages) {
        chatHistories.put(userId, new ArrayList<>(messages));
    }
    
    public void clearChatHistory(String userId) {
        chatHistories.remove(userId);
    }
    
    public List<ChatMessage> getChatHistory(String userId) {
        return chatHistories.getOrDefault(userId, new ArrayList<>());
    }
}