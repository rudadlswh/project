package com.crossfit.web.dto;

import com.crossfit.domain.ReservationStatus;
import com.crossfit.domain.TimeSlot;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class SessionDtos {
    public static class CreateSessionRequest {
        @NotNull
        public LocalDate date;
        @NotNull
        public TimeSlot timeSlot;
        public Integer capacity;
    }

    public static class UpdateCapacityRequest {
        public Integer capacity;
    }

    public static class SessionResponse {
        public Long id;
        public LocalDate date;
        public TimeSlot timeSlot;
        public Integer capacity;
        public long bookedCount;
        public long waitlistCount;
        public ReservationStatus myStatus;
        public Integer myWaitlistPosition;
    }
}
