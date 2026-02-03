package com.example.crossfit.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

public record BulkRecordRequest(
        @NotEmpty List<String> members,
        @NotBlank String recordType,
        @NotBlank String value,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate recordDate,
        @NotBlank String wodTitle
) {
}
