package com.example.crossfit.booking;

import java.time.LocalDate;

public record SessionSummary(
        Long id,
        LocalDate sessionDate,
        TimeSlot timeSlot,
        Integer capacity,
        long reservedCount,
        ReservationStatus myStatus
) {
}
