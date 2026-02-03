package com.example.crossfit.booking;

import com.example.crossfit.member.User;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findBySessionId(Long sessionId);

    Optional<Reservation> findBySessionIdAndUserId(Long sessionId, Long userId);

    @Query("select count(r) from Reservation r where r.session.id = :sessionId and r.status = 'RESERVED'")
    long countReserved(@Param("sessionId") Long sessionId);

    @Query("select count(r) from Reservation r where r.session.id = :sessionId and r.status = 'WAITLIST'")
    long countWaitlist(@Param("sessionId") Long sessionId);

    @Query("select r from Reservation r where r.session.id = :sessionId and r.status <> 'CANCELED' order by r.createdAt")
    List<Reservation> findActiveBySessionId(@Param("sessionId") Long sessionId);

    List<Reservation> findBySessionIdAndStatusOrderByCreatedAt(Long sessionId, ReservationStatus status);

    Optional<Reservation> findFirstBySessionIdAndStatusOrderByCreatedAt(Long sessionId, ReservationStatus status);

    @Query("select count(r) from Reservation r where r.session.id = :sessionId and r.status = 'WAITLIST' and r.createdAt < :createdAt")
    long countWaitlistBefore(@Param("sessionId") Long sessionId, @Param("createdAt") Instant createdAt);

    List<Reservation> findByUser(User user);
}
