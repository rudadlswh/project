package com.example.crossfit.attendance;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.YearMonth;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/attendance", "/attendance"})
public class AttendanceController {
    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping("/monthly")
    public ResponseEntity<AttendanceSummaryResponse> getMonthlySummary(
            @RequestParam("month") String month,
            Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        YearMonth yearMonth = YearMonth.parse(month);
        return ResponseEntity.ok(attendanceService.getMonthlySummaryResponse(userId, yearMonth));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','COACH')")
    public ResponseEntity<Attendance> markAttendance(@RequestBody AttendanceRequest request) {
        Attendance attendance = attendanceService.markAttendance(request.userId(), request.date());
        return ResponseEntity.ok(attendance);
    }

    public record AttendanceRequest(
            @NotNull Long userId,
            @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
    }
}
