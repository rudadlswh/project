package com.example.crossfit.booking;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/sessions", "/sessions"})
public class BookingController {
    private final BookingService bookingService;
    private final ReservationRepository reservationRepository;

    public BookingController(BookingService bookingService, ReservationRepository reservationRepository) {
        this.bookingService = bookingService;
        this.reservationRepository = reservationRepository;
    }

    @GetMapping
    public ResponseEntity<List<SessionResponse>> getSessions(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        List<Session> sessions = bookingService.getOrCreateSessions(date);
        List<SessionResponse> summaries = sessions.stream().map(session -> {
            long reservedCount = reservationRepository.countReserved(session.getId());
            long waitlistCount = reservationRepository.countWaitlist(session.getId());
            Reservation reservation = reservationRepository
                    .findBySessionIdAndUserId(session.getId(), userId)
                    .filter(found -> found.getStatus() != ReservationStatus.CANCELED)
                    .orElse(null);
            ReservationStatus status = reservation == null ? null : reservation.getStatus();
            Integer waitlistPosition = null;
            if (reservation != null && reservation.getStatus() == ReservationStatus.WAITLIST) {
                long ahead = reservationRepository.countWaitlistBefore(session.getId(), reservation.getCreatedAt());
                waitlistPosition = Math.toIntExact(ahead + 1);
            }
            return SessionResponse.from(session, reservedCount, waitlistCount, status, waitlistPosition);
        }).toList();
        return ResponseEntity.ok(summaries);
    }

    @PostMapping("/{id}/reservations")
    public ResponseEntity<ReservationResponse> reserve(@PathVariable("id") Long sessionId,
                                                       Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        Reservation reservation = bookingService.reserve(sessionId, userId);
        return ResponseEntity.ok(ReservationResponse.from(reservation));
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<ReservationResponse> cancel(@PathVariable("id") Long reservationId,
                                                      Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        Reservation reservation = bookingService.cancelReservation(reservationId, userId);
        return ResponseEntity.ok(ReservationResponse.from(reservation));
    }

    @GetMapping("/me")
    public ResponseEntity<List<ReservationResponse>> myReservations(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        List<ReservationResponse> reservations = bookingService.getReservationsForUser(userId)
                .stream()
                .map(ReservationResponse::from)
                .toList();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{id}/reservations")
    @PreAuthorize("hasAnyRole('ADMIN','COACH')")
    public ResponseEntity<List<SessionReservationResponse>> reservations(@PathVariable("id") Long sessionId) {
        List<SessionReservationResponse> responses = bookingService.getActiveReservations(sessionId)
                .stream()
                .map(SessionReservationResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{id}/capacity")
    @PreAuthorize("hasAnyRole('ADMIN','COACH')")
    public ResponseEntity<SessionResponse> updateCapacity(@PathVariable("id") Long sessionId,
                                                          @RequestBody CapacityRequest request) {
        Session session = bookingService.updateCapacity(sessionId, request.capacity());
        long reservedCount = reservationRepository.countReserved(session.getId());
        long waitlistCount = reservationRepository.countWaitlist(session.getId());
        return ResponseEntity.ok(SessionResponse.from(session, reservedCount, waitlistCount, null, null));
    }

    public record CapacityRequest(@NotNull Integer capacity) {
    }
}
