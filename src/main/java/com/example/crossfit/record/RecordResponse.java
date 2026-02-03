package com.example.crossfit.record;

import java.time.LocalDate;
import java.time.ZoneId;

public record RecordResponse(
        Long id,
        Long wodId,
        String type,
        String value,
        String imageUrl,
        String recordDate
) {
    public static RecordResponse from(WorkoutRecord record) {
        LocalDate date = record.getRecordDate();
        if (date == null) {
            date = LocalDate.ofInstant(record.getCreatedAt(), ZoneId.systemDefault());
        }
        return new RecordResponse(
                record.getId(),
                record.getWod() == null ? null : record.getWod().getId(),
                record.getType().name(),
                record.getValue(),
                record.getImageUrl(),
                date.toString()
        );
    }
}
