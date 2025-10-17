package com.mediconnect.dto;

import java.util.List;

public class ChatRequest {
    private List<ChatMessage> messages;
    private String userRole; // "DOCTOR" or "PATIENT"

    public ChatRequest() {
    }

    public ChatRequest(List<ChatMessage> messages, String userRole) {
        this.messages = messages;
        this.userRole = userRole;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}
