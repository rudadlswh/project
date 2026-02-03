package com.example.crossfit.booking;

public record SessionResponse(
        Long id,
        String date,
        String timeSlot,
        Integer capacity,
        long bookedCount,
        long waitlistCount,
        String myStatus,
        Integer myWaitlistPosition
) {
    public static SessionResponse from(Session session,
                                       long bookedCount,
                                       long waitlistCount,
                                       ReservationStatus myStatus,
                                       Integer myWaitlistPosition) {
        return new SessionResponse(
                session.getId(),
                session.getSessionDate().toString(),
                session.getTimeSlot().getDisplay(),
                session.getCapacity(),
                bookedCount,
                waitlistCount,
                myStatus == null ? null : myStatus.name(),
                myWaitlistPosition
        );
    }
}
