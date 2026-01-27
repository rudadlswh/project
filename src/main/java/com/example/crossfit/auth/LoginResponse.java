package com.example.crossfit.auth;

public record LoginResponse(
        String accessToken,
        String role,
        String email
) {
}
