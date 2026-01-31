package com.crossfit.web.dto;

import com.crossfit.domain.RecordType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class RecordDtos {
    public static class CreateRecordRequest {
        public Long wodId;
        @NotNull
        public RecordType type;
        @NotBlank
        public String value;
        @NotNull
        public LocalDate recordDate;
    }

    public static class RecordResponse {
        public Long id;
        public Long wodId;
        public RecordType type;
        public String value;
        public LocalDate recordDate;
    }
}
