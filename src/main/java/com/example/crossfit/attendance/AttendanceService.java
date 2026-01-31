package com.example.crossfit.attendance;

import com.example.crossfit.member.User;
import com.example.crossfit.member.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    public AttendanceService(AttendanceRepository attendanceRepository, UserRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Attendance markAttendance(Long userId, LocalDate date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return attendanceRepository.save(new Attendance(user, date));
    }

    public AttendanceSummary getMonthlySummary(Long userId, YearMonth month) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        List<Attendance> attendances = attendanceRepository.findByUserAndAttendedDateBetween(user, start, end);
        List<LocalDate> days = attendances.stream().map(Attendance::getAttendedDate).distinct().sorted().toList();
        int daysInMonth = month.lengthOfMonth();
        double rate = daysInMonth == 0 ? 0 : (double) days.size() / daysInMonth;
        return new AttendanceSummary(month.toString(), days, rate);
    }
}
