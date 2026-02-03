package com.example.crossfit.booking;

public record ReservationActionResponse(
        Long reservationId,
        String status,
        Integer waitlistPosition,
        String message
) {
    public static ReservationActionResponse of(Reservation reservation, Integer waitlistPosition, String message) {
        return new ReservationActionResponse(
                reservation.getId(),
                reservation.getStatus().name(),
                waitlistPosition,
                message
        );
    }
}
