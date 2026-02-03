package com.example.crossfit.booking;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findBySessionDate(LocalDate sessionDate);

    Optional<Session> findBySessionDateAndTimeSlot(LocalDate sessionDate, TimeSlot timeSlot);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Session s where s.sessionDate = :sessionDate and s.timeSlot = :timeSlot")
    Optional<Session> findBySessionDateAndTimeSlotForUpdate(@Param("sessionDate") LocalDate sessionDate,
                                                            @Param("timeSlot") TimeSlot timeSlot);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Session s where s.id = :id")
    Optional<Session> findByIdForUpdate(@Param("id") Long id);
}
