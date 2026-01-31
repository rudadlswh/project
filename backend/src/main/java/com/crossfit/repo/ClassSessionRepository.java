package com.crossfit.repo;

import com.crossfit.domain.ClassSession;
import com.crossfit.domain.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ClassSessionRepository extends JpaRepository<ClassSession, Long> {
    List<ClassSession> findBySessionDate(LocalDate date);
    Optional<ClassSession> findBySessionDateAndTimeSlot(LocalDate date, TimeSlot timeSlot);
}
