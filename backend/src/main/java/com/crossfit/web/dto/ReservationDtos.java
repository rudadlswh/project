package com.crossfit.web.dto;

import com.crossfit.domain.ReservationStatus;
import com.crossfit.domain.TimeSlot;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class ReservationDtos {
    public static class ReserveRequest {
        @NotNull
        public LocalDate date;
        @NotNull
        public TimeSlot timeSlot;
    }

    public static class ReservationResponse {
        public Long reservationId;
        public ReservationStatus status;
        public Integer waitlistPosition;
        public String message;
    }
}
