package com.example.crossfit.announcement;

import jakarta.validation.constraints.NotBlank;

public record AnnouncementRequest(
        @NotBlank String title,
        @NotBlank String body
) {
}
