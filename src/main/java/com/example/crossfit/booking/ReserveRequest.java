package com.example.crossfit.booking;

import jakarta.validation.constraints.NotBlank;

public record ReserveRequest(
        @NotBlank String date,
        @NotBlank String timeSlot
) {
}
