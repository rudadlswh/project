package com.example.crossfit.booking;

import java.time.Instant;

public record ReservationResponse(
        Long id,
        Long sessionId,
        ReservationStatus status,
        Instant createdAt
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getSession().getId(),
                reservation.getStatus(),
                reservation.getCreatedAt());
    }
}
