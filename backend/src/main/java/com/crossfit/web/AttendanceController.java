package com.crossfit.web;

import com.crossfit.domain.User;
import com.crossfit.service.AttendanceService;
import com.crossfit.service.UserService;
import com.crossfit.web.dto.AttendanceDtos;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;
    private final UserService userService;

    public AttendanceController(AttendanceService attendanceService, UserService userService) {
        this.attendanceService = attendanceService;
        this.userService = userService;
    }

    @GetMapping("/monthly")
    public AttendanceDtos.AttendanceSummaryResponse monthly(@RequestParam("month") YearMonth month) {
        User user = userService.getCurrentUser();
        return attendanceService.getMonthlySummary(user, month);
    }

    @PostMapping("/mark")
    @PreAuthorize("hasAnyRole('ADMIN','COACH')")
    public void mark(@RequestParam("userId") Long userId, @RequestParam("date") LocalDate date) {
        attendanceService.markAttendance(userService.requireById(userId), date);
    }
}
