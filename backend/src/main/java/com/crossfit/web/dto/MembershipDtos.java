package com.crossfit.web.dto;

import com.crossfit.domain.MembershipType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class MembershipDtos {
    public static class UpsertMembershipRequest {
        @NotNull
        public MembershipType type;
        public LocalDate startDate;
        public LocalDate endDate;
        public Integer remainingCount;
    }

    public static class MembershipResponse {
        public MembershipType type;
        public LocalDate startDate;
        public LocalDate endDate;
        public Integer remainingCount;
        public Integer remainingDays;
    }
}
