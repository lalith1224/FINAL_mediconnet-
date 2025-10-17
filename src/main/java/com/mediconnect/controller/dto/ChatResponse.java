package com.mediconnect.dto;

public class ChatResponse {
    private String message;
    private String role;
    private boolean success;
    private String error;

    public ChatResponse() {
    }

    public ChatResponse(String message, String role, boolean success) {
        this.message = message;
        this.role = role;
        this.success = success;
    }

    public static ChatResponse success(String message) {
        ChatResponse response = new ChatResponse();
        response.setMessage(message);
        response.setRole("assistant");
        response.setSuccess(true);
        return response;
    }

    public static ChatResponse error(String error) {
        ChatResponse response = new ChatResponse();
        response.setError(error);
        response.setSuccess(false);
        return response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
