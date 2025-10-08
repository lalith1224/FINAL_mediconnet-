package com.mediconnect.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mediconnect.dto.ChatMessage;
import com.mediconnect.dto.ChatRequest;
import com.mediconnect.dto.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

@Service
public class ChatbotService {

    @Value("${openrouter.api.key:}")
    private String apiKey;

    @Value("${openrouter.api.url:https://openrouter.ai/api/v1/chat/completions}")
    private String apiUrl;

    @Value("${openrouter.model:meta-llama/llama-3.1-8b-instruct:free}")
    private String model;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ChatbotService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public ChatResponse chat(ChatRequest chatRequest) {
        try {
            if (apiKey == null || apiKey.isEmpty()) {
                return ChatResponse.error("OpenRouter API key is not configured. Please add it to application.properties");
            }

            // Build the request payload
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("model", model);

            // Add system message based on user role
            ArrayNode messages = objectMapper.createArrayNode();
            
            String systemPrompt = getSystemPrompt(chatRequest.getUserRole());
            ObjectNode systemMessage = objectMapper.createObjectNode();
            systemMessage.put("role", "system");
            systemMessage.put("content", systemPrompt);
            messages.add(systemMessage);

            // Add conversation messages
            for (ChatMessage msg : chatRequest.getMessages()) {
                ObjectNode messageNode = objectMapper.createObjectNode();
                messageNode.put("role", msg.getRole());
                messageNode.put("content", msg.getContent());
                messages.add(messageNode);
            }

            payload.set("messages", messages);
            payload.put("temperature", 0.7);
            payload.put("max_tokens", 1000);

            // Create HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .header("HTTP-Referer", "https://mediconnect.app")
                    .header("X-Title", "MediConnect")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                    .timeout(Duration.ofSeconds(30))
                    .build();

            // Send request
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode responseJson = objectMapper.readTree(response.body());
                String assistantMessage = responseJson
                        .path("choices")
                        .get(0)
                        .path("message")
                        .path("content")
                        .asText();

                return ChatResponse.success(assistantMessage);
            } else {
                JsonNode errorJson = objectMapper.readTree(response.body());
                String errorMessage = errorJson.path("error").path("message").asText("Unknown error occurred");
                return ChatResponse.error("API Error: " + errorMessage);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ChatResponse.error("Failed to communicate with AI service: " + e.getMessage());
        }
    }

    private String getSystemPrompt(String userRole) {
        if ("DOCTOR".equalsIgnoreCase(userRole)) {
            return "You are a helpful AI assistant for doctors in the MediConnect healthcare system. " +
                    "You can help with medical information, patient management queries, appointment scheduling, " +
                    "prescription guidelines, and general healthcare administration. " +
                    "Always provide accurate, professional medical information and remind doctors to verify " +
                    "critical information independently. Do not provide specific diagnoses or treatment plans " +
                    "without proper context.";
        } else if ("PATIENT".equalsIgnoreCase(userRole)) {
            return "You are a helpful AI assistant for patients in the MediConnect healthcare system. " +
                    "You can help answer general health questions, explain medical terms, provide information " +
                    "about appointments and prescriptions, and offer wellness tips. " +
                    "Always remind patients that you are not a replacement for professional medical advice " +
                    "and they should consult their doctor for specific medical concerns. " +
                    "Be empathetic, clear, and supportive in your responses.";
        } else {
            return "You are a helpful AI assistant for the MediConnect healthcare system. " +
                    "Provide helpful, accurate, and professional information.";
        }
    }
}
