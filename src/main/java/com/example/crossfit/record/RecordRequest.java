package com.example.crossfit.record;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record RecordRequest(
        Long wodId,
        @NotBlank String type,
        @NotBlank String value,
        String imageUrl,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate recordDate
) {
}
