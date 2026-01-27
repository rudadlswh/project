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
@RequestMapping("/api/sessions")
public class BookingController {
    private final BookingService bookingService;
    private final ReservationRepository reservationRepository;

    public BookingController(BookingService bookingService, ReservationRepository reservationRepository) {
        this.bookingService = bookingService;
        this.reservationRepository = reservationRepository;
    }

    @GetMapping
    public ResponseEntity<List<SessionSummary>> getSessions(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        List<Session> sessions = bookingService.getOrCreateSessions(date);
        List<SessionSummary> summaries = sessions.stream().map(session -> {
            long reservedCount = reservationRepository.countReserved(session.getId());
            ReservationStatus status = reservationRepository
                    .findBySessionIdAndUserId(session.getId(), userId)
                    .map(Reservation::getStatus)
                    .orElse(null);
            return new SessionSummary(session.getId(), session.getSessionDate(), session.getTimeSlot(), session.getCapacity(),
                    reservedCount, status);
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

    @PatchMapping("/{id}/capacity")
    @PreAuthorize("hasAnyRole('ADMIN','COACH')")
    public ResponseEntity<SessionSummary> updateCapacity(@PathVariable("id") Long sessionId,
                                                         @RequestBody CapacityRequest request) {
        Session session = bookingService.updateCapacity(sessionId, request.capacity());
        long reservedCount = reservationRepository.countReserved(session.getId());
        return ResponseEntity.ok(new SessionSummary(session.getId(), session.getSessionDate(), session.getTimeSlot(),
                session.getCapacity(), reservedCount, null));
    }

    public record CapacityRequest(@NotNull Integer capacity) {
    }
}
