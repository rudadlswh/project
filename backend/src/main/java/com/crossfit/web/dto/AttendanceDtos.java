package com.crossfit.web.dto;

import java.time.LocalDate;
import java.util.List;

public class AttendanceDtos {
    public static class AttendanceSummaryResponse {
        public List<LocalDate> dates;
        public int totalDays;
        public int weekdaysInMonth;
        public double attendanceRate;
    }
}
