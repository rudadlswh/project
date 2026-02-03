package com.example.crossfit.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateCoachRequest(
        @Email @NotBlank String email,
        @NotBlank String displayName
) {
}
