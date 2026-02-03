package com.example.crossfit.booking;

import java.time.LocalDateTime;
import java.time.ZoneId;

public record SessionReservationResponse(
        Long reservationId,
        Long userId,
        String displayName,
        String status,
        String createdAt
) {
    public static SessionReservationResponse from(Reservation reservation) {
        String displayName = reservation.getUser().getDisplayName() == null
                ? reservation.getUser().getEmail()
                : reservation.getUser().getDisplayName();
        LocalDateTime createdAt = LocalDateTime.ofInstant(reservation.getCreatedAt(), ZoneId.systemDefault());
        return new SessionReservationResponse(
                reservation.getId(),
                reservation.getUser().getId(),
                displayName,
                reservation.getStatus().name(),
                createdAt.toString()
        );
    }
}
