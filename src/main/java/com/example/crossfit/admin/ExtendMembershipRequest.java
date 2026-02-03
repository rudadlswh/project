package com.example.crossfit.admin;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ExtendMembershipRequest(
        @NotBlank String query,
        @Min(1) int days
) {
}
