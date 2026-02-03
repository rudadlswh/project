package com.example.crossfit.attendance;

import java.util.List;

public record AttendanceSummaryResponse(
        List<String> dates,
        int totalDays,
        int weekdaysInMonth,
        double attendanceRate
) {
}
