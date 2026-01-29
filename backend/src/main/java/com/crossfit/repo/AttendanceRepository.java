package com.crossfit.repo;

import com.crossfit.domain.Attendance;
import com.crossfit.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByUserAndDateBetween(User user, LocalDate start, LocalDate end);
    boolean existsByUserAndDate(User user, LocalDate date);
}
