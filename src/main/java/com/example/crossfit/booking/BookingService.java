package com.example.crossfit.booking;

import com.example.crossfit.member.User;
import com.example.crossfit.member.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {
    private final SessionRepository sessionRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    public BookingService(SessionRepository sessionRepository,
                          ReservationRepository reservationRepository,
                          UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public List<Session> getOrCreateSessions(LocalDate date) {
        List<Session> sessions = sessionRepository.findBySessionDate(date);
        if (!sessions.isEmpty()) {
            sessions.sort(Comparator.comparing(Session::getTimeSlot));
            return sessions;
        }
        for (TimeSlot slot : TimeSlot.defaultSlots()) {
            LocalDateTime cutoff = LocalDateTime.of(date, slot.getTime()).minusHours(1);
            sessionRepository.save(new Session(date, slot, null, cutoff));
        }
        return sessionRepository.findBySessionDate(date).stream()
                .sorted(Comparator.comparing(Session::getTimeSlot))
                .toList();
    }

    @Transactional
    public Reservation reserve(Long sessionId, Long userId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        reservationRepository.findBySessionIdAndUserId(sessionId, userId)
                .filter(reservation -> reservation.getStatus() != ReservationStatus.CANCELED)
                .ifPresent(reservation -> {
                    throw new IllegalStateException("Already reserved");
                });

        long reservedCount = reservationRepository.countReserved(sessionId);
        boolean hasCapacityLimit = session.getCapacity() != null;
        ReservationStatus status = ReservationStatus.RESERVED;
        if (hasCapacityLimit && reservedCount >= session.getCapacity()) {
            status = ReservationStatus.WAITLIST;
        }
        return reservationRepository.save(new Reservation(user, session, status));
    }

    @Transactional
    public Reservation cancelReservation(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));
        if (!reservation.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Only owner can cancel");
        }
        if (LocalDateTime.now().isAfter(reservation.getSession().getCutoffAt())) {
            throw new IllegalStateException("Cancellation deadline passed");
        }
        reservation.setStatus(ReservationStatus.CANCELED);
        return reservation;
    }

    public List<Reservation> getReservationsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return reservationRepository.findByUser(user);
    }

    @Transactional
    public Session updateCapacity(Long sessionId, Integer capacity) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));
        session.setCapacity(capacity);
        return session;
    }
}
