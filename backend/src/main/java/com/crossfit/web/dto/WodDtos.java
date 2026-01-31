package com.crossfit.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class WodDtos {
    public static class CreateWodRequest {
        @NotNull
        public LocalDate date;
        @NotBlank
        public String title;
        @NotBlank
        public String type;
        @NotBlank
        public String description;
    }

    public static class WodResponse {
        public Long id;
        public LocalDate date;
        public String title;
        public String type;
        public String description;
    }
}
