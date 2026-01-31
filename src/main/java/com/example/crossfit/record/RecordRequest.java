package com.example.crossfit.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RecordRequest(
        @NotNull Long wodId,
        @NotNull RecordType type,
        @NotBlank String value
) {
}
