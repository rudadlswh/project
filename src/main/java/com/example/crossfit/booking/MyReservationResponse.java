package com.example.crossfit.booking;

public record MyReservationResponse(
        Long reservationId,
        Long sessionId,
        String date,
        String timeSlot,
        String status
) {
    public static MyReservationResponse from(Reservation reservation) {
        return new MyReservationResponse(
                reservation.getId(),
                reservation.getSession().getId(),
                reservation.getSession().getSessionDate().toString(),
                reservation.getSession().getTimeSlot().getDisplay(),
                reservation.getStatus().name()
        );
    }
}
