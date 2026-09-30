package com.example.securitylab.dto;

public record AuthResponse(String token, String username, long expiresInMs) {
}
