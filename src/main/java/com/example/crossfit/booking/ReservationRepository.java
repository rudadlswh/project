package com.example.crossfit.booking;

import com.example.crossfit.member.User;
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

    List<Reservation> findByUser(User user);
}
