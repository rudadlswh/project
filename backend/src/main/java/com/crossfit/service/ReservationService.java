package com.crossfit.service;

import com.crossfit.domain.ClassSession;
import com.crossfit.domain.Reservation;
import com.crossfit.domain.ReservationStatus;
import com.crossfit.domain.TimeSlot;
import com.crossfit.domain.User;
import com.crossfit.repo.ReservationRepository;
import com.crossfit.web.dto.ReservationDtos;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final SessionService sessionService;

    public ReservationService(ReservationRepository reservationRepository, SessionService sessionService) {
        this.reservationRepository = reservationRepository;
        this.sessionService = sessionService;
    }

    @Transactional
    public ReservationDtos.ReservationResponse reserve(User user, LocalDate date, TimeSlot slot) {
        sessionService.ensureDailySessions(date);
        ClassSession session = sessionService.requireSession(date, slot);

        Reservation existing = reservationRepository.findByUserAndSession(user, session).orElse(null);
        if (existing != null && existing.getStatus() != ReservationStatus.CANCELLED) {
            throw new IllegalStateException("Already reserved");
        }

        boolean unlimited = session.getCapacity() == null || session.getCapacity() == 0;
        long bookedCount = reservationRepository.countBySessionAndStatus(session, ReservationStatus.BOOKED);

        ReservationStatus status;
        Integer waitlistPosition = null;
        if (unlimited || bookedCount < session.getCapacity()) {
            status = ReservationStatus.BOOKED;
        } else {
            status = ReservationStatus.WAITLISTED;
            waitlistPosition = nextWaitlistPosition(session);
        }

        Reservation reservation;
        if (existing == null) {
            reservation = new Reservation(user, session, status, waitlistPosition);
            reservationRepository.save(reservation);
        } else {
            existing.setStatus(status);
            existing.setWaitlistPosition(waitlistPosition);
            reservation = existing;
        }

        ReservationDtos.ReservationResponse res = new ReservationDtos.ReservationResponse();
        res.reservationId = reservation.getId();
        res.status = reservation.getStatus();
        res.waitlistPosition = reservation.getWaitlistPosition();
        res.message = status == ReservationStatus.BOOKED ? "Booked" : "Waitlisted";
        return res;
    }

    @Transactional
    public ReservationDtos.ReservationResponse cancel(User user, LocalDate date, TimeSlot slot) {
        ClassSession session = sessionService.requireSession(date, slot);
        Reservation reservation = reservationRepository.findByUserAndSession(user, session)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        LocalDateTime sessionStart = LocalDateTime.of(date, slot.getTime());
        if (LocalDateTime.now().isAfter(sessionStart.minusHours(1))) {
            throw new IllegalStateException("Cancellation window closed");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setWaitlistPosition(null);

        promoteWaitlistIfNeeded(session);

        ReservationDtos.ReservationResponse res = new ReservationDtos.ReservationResponse();
        res.reservationId = reservation.getId();
        res.status = reservation.getStatus();
        res.message = "Cancelled";
        return res;
    }

    private Integer nextWaitlistPosition(ClassSession session) {
        return reservationRepository.findBySessionAndStatusOrderByWaitlistPositionAsc(session, ReservationStatus.WAITLISTED)
                .stream()
                .map(Reservation::getWaitlistPosition)
                .filter(p -> p != null)
                .max(Comparator.naturalOrder())
                .map(p -> p + 1)
                .orElse(1);
    }

    private void promoteWaitlistIfNeeded(ClassSession session) {
        boolean unlimited = session.getCapacity() == null || session.getCapacity() == 0;
        if (unlimited) {
            return;
        }
        long bookedCount = reservationRepository.countBySessionAndStatus(session, ReservationStatus.BOOKED);
        if (bookedCount >= session.getCapacity()) {
            return;
        }
        List<Reservation> waitlist = reservationRepository
                .findBySessionAndStatusOrderByWaitlistPositionAsc(session, ReservationStatus.WAITLISTED);
        if (waitlist.isEmpty()) {
            return;
        }
        Reservation next = waitlist.get(0);
        next.setStatus(ReservationStatus.BOOKED);
        next.setWaitlistPosition(null);
    }
}
