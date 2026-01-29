package com.crossfit.web.dto;

import jakarta.validation.constraints.NotBlank;

public class NoticeDtos {
    public static class CreateNoticeRequest {
        @NotBlank
        public String title;
        @NotBlank
        public String content;
    }

    public static class NoticeResponse {
        public Long id;
        public String title;
        public String content;
        public String createdBy;
    }
}
