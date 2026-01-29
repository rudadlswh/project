package com.crossfit.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {
    public static class RegisterRequest {
        @Email
        @NotBlank
        public String email;
        @NotBlank
        @Size(min = 6, max = 100)
        public String password;
        @NotBlank
        public String displayName;
    }

    public static class LoginRequest {
        @Email
        @NotBlank
        public String email;
        @NotBlank
        public String password;
    }

    public static class AuthResponse {
        public String accessToken;
        public String role;
        public String displayName;
    }
}
