package com.crossfit.service;

import com.crossfit.domain.Attendance;
import com.crossfit.domain.User;
import com.crossfit.repo.AttendanceRepository;
import com.crossfit.web.dto.AttendanceDtos;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;

    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    @Transactional
    public void markAttendance(User user, LocalDate date) {
        if (attendanceRepository.existsByUserAndDate(user, date)) {
            return;
        }
        attendanceRepository.save(new Attendance(user, date));
    }

    public AttendanceDtos.AttendanceSummaryResponse getMonthlySummary(User user, YearMonth month) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        List<Attendance> list = attendanceRepository.findByUserAndDateBetween(user, start, end);
        int weekdays = countWeekdays(start, end);
        int total = list.size();

        AttendanceDtos.AttendanceSummaryResponse res = new AttendanceDtos.AttendanceSummaryResponse();
        res.dates = list.stream().map(Attendance::getDate).sorted().collect(Collectors.toList());
        res.totalDays = total;
        res.weekdaysInMonth = weekdays;
        res.attendanceRate = weekdays == 0 ? 0.0 : (double) total / weekdays;
        return res;
    }

    private int countWeekdays(LocalDate start, LocalDate end) {
        int count = 0;
        LocalDate cursor = start;
        while (!cursor.isAfter(end)) {
            DayOfWeek dow = cursor.getDayOfWeek();
            if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY) {
                count++;
            }
            cursor = cursor.plusDays(1);
        }
        return count;
    }
}
