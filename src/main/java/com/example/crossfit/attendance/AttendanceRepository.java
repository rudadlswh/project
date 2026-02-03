package com.example.crossfit.attendance;

import com.example.crossfit.member.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByUserAndAttendedDateBetween(User user, LocalDate start, LocalDate end);

    Optional<Attendance> findByUserAndAttendedDate(User user, LocalDate date);
}
