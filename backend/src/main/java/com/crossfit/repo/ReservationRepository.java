package com.crossfit.repo;

import com.crossfit.domain.ClassSession;
import com.crossfit.domain.Reservation;
import com.crossfit.domain.ReservationStatus;
import com.crossfit.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByUserAndSession(User user, ClassSession session);
    long countBySessionAndStatus(ClassSession session, ReservationStatus status);
    List<Reservation> findBySessionAndStatusOrderByWaitlistPositionAsc(ClassSession session, ReservationStatus status);
    List<Reservation> findByUser(User user);
}
