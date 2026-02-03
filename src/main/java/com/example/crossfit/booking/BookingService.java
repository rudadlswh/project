package com.example.crossfit.booking;

import com.example.crossfit.member.User;
import com.example.crossfit.member.UserRepository;
import com.example.crossfit.member.Membership;
import com.example.crossfit.member.MembershipService;
import com.example.crossfit.common.Role;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class BookingService {
    private final SessionRepository sessionRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final MembershipService membershipService;

    public BookingService(SessionRepository sessionRepository,
                          ReservationRepository reservationRepository,
                          UserRepository userRepository,
                          MembershipService membershipService) {
        this.sessionRepository = sessionRepository;
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.membershipService = membershipService;
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
        Session session = sessionRepository.findByIdForUpdate(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));
        return reserve(session, userId);
    }

    @Transactional
    public Reservation cancelReservation(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));
        if (!reservation.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owner can cancel");
        }
        if (LocalDateTime.now().isAfter(reservation.getSession().getCutoffAt())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cancellation deadline passed");
        }
        if (reservation.getStatus() == ReservationStatus.RESERVED) {
            Membership membership = membershipService.getLatestMembership(userId);
            membershipService.refundIfCount(membership);
        }
        reservation.setStatus(ReservationStatus.CANCELED);
        promoteWaitlist(reservation.getSession());
        return reservation;
    }

    @Transactional
    public Reservation reserve(LocalDate date, TimeSlot timeSlot, Long userId) {
        Session session = sessionRepository.findBySessionDateAndTimeSlotForUpdate(date, timeSlot)
                .orElseGet(() -> {
                    getOrCreateSessions(date);
                    return sessionRepository.findBySessionDateAndTimeSlotForUpdate(date, timeSlot)
                            .orElseThrow(() -> new EntityNotFoundException("Session not found"));
                });
        return reserve(session, userId);
    }

    @Transactional
    public Reservation cancelReservation(LocalDate date, TimeSlot timeSlot, Long userId) {
        Session session = sessionRepository.findBySessionDateAndTimeSlotForUpdate(date, timeSlot)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));
        Reservation reservation = reservationRepository.findBySessionIdAndUserId(session.getId(), userId)
                .filter(existing -> existing.getStatus() != ReservationStatus.CANCELED)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));
        if (LocalDateTime.now().isAfter(session.getCutoffAt())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cancellation deadline passed");
        }
        if (reservation.getStatus() == ReservationStatus.RESERVED) {
            Membership membership = membershipService.getLatestMembership(userId);
            membershipService.refundIfCount(membership);
        }
        reservation.setStatus(ReservationStatus.CANCELED);
        promoteWaitlist(session);
        return reservation;
    }

    public List<Reservation> getReservationsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return reservationRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public List<Reservation> getActiveReservations(Long sessionId) {
        sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));
        return reservationRepository.findActiveBySessionId(sessionId);
    }

    @Transactional
    public Session updateCapacity(Long sessionId, Integer capacity) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));
        session.setCapacity(capacity);
        promoteWaitlist(session);
        return session;
    }

    private Reservation reserve(Session session, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Membership membership = null;
        if (user.getRole() == Role.MEMBER) {
            membership = membershipService.ensureActiveMembershipForReservation(userId, session.getSessionDate());
        }

        Reservation existing = reservationRepository.findBySessionIdAndUserId(session.getId(), userId)
                .orElse(null);
        if (existing != null && existing.getStatus() != ReservationStatus.CANCELED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already reserved");
        }

        long reservedCount = reservationRepository.countReserved(session.getId());
        boolean hasCapacityLimit = session.getCapacity() != null;
        ReservationStatus status = ReservationStatus.RESERVED;
        if (hasCapacityLimit && reservedCount >= session.getCapacity()) {
            status = ReservationStatus.WAITLIST;
        }
        try {
            Reservation reservation;
            if (existing != null) {
                existing.setStatus(status);
                existing.refreshCreatedAt();
                reservation = existing;
            } else {
                reservation = new Reservation(user, session, status);
            }
            if (status == ReservationStatus.RESERVED) {
                membershipService.consumeIfCount(membership);
            }
            return reservationRepository.save(reservation);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already reserved");
        }
    }

    private void promoteWaitlist(Session session) {
        Integer capacity = session.getCapacity();
        long reservedCount = reservationRepository.countReserved(session.getId());
        List<Reservation> waitlist = reservationRepository
                .findBySessionIdAndStatusOrderByCreatedAt(session.getId(), ReservationStatus.WAITLIST);
        for (Reservation next : waitlist) {
            if (capacity != null && reservedCount >= capacity) {
                break;
            }
            try {
                Membership membership = null;
                if (next.getUser().getRole() == Role.MEMBER) {
                    membership = membershipService.ensureActiveMembershipForReservation(
                            next.getUser().getId(),
                            session.getSessionDate()
                    );
                }
                membershipService.consumeIfCount(membership);
                next.setStatus(ReservationStatus.RESERVED);
                reservedCount++;
            } catch (ResponseStatusException ex) {
                next.setStatus(ReservationStatus.CANCELED);
            }
        }
    }
}
