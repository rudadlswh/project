package com.example.crossfit.attendance;

import java.time.LocalDate;
import java.util.List;

public record AttendanceSummary(
        String month,
        List<LocalDate> attendedDays,
        double attendanceRate
) {
}
