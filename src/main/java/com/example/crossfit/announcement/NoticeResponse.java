package com.example.crossfit.announcement;

import java.time.LocalDateTime;
import java.time.ZoneId;

public record NoticeResponse(
        Long id,
        String title,
        String content,
        String createdBy,
        String createdAt
) {
    public static NoticeResponse from(Announcement announcement) {
        String author = announcement.getCreatedBy().getDisplayName() == null
                ? announcement.getCreatedBy().getEmail()
                : announcement.getCreatedBy().getDisplayName();
        LocalDateTime createdAt = LocalDateTime.ofInstant(announcement.getCreatedAt(), ZoneId.systemDefault());
        return new NoticeResponse(
                announcement.getId(),
                announcement.getTitle(),
                announcement.getBody(),
                author,
                createdAt.toString()
        );
    }
}
