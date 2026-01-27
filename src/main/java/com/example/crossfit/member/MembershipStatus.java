package com.example.crossfit.member;

import java.time.LocalDate;

public record MembershipStatus(
        MembershipType type,
        LocalDate startDate,
        LocalDate endDate,
        Integer remainingCount,
        long remainingDays
) {
}
