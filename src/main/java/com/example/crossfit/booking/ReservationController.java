package com.example.crossfit.booking;

import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping({"/api/reservations", "/reservations"})
public class ReservationController {
    private final BookingService bookingService;
    private final ReservationRepository reservationRepository;

    public ReservationController(BookingService bookingService, ReservationRepository reservationRepository) {
        this.bookingService = bookingService;
        this.reservationRepository = reservationRepository;
    }

    @PostMapping
    public ResponseEntity<ReservationActionResponse> reserve(@Valid @RequestBody ReserveRequest request,
                                                             Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        TimeSlot timeSlot = parseTimeSlot(request.timeSlot());
        LocalDate date = parseDate(request.date());
        Reservation reservation = bookingService.reserve(date, timeSlot, userId);
        Integer waitlistPosition = null;
        if (reservation.getStatus() == ReservationStatus.WAITLIST) {
            long ahead = reservationRepository.countWaitlistBefore(reservation.getSession().getId(), reservation.getCreatedAt());
            waitlistPosition = Math.toIntExact(ahead + 1);
        }
        String message = reservation.getStatus() == ReservationStatus.WAITLIST ? "대기 예약이 완료되었습니다." : "예약이 완료되었습니다.";
        return ResponseEntity.ok(ReservationActionResponse.of(reservation, waitlistPosition, message));
    }

    @DeleteMapping
    public ResponseEntity<ReservationActionResponse> cancel(@RequestParam("date") String date,
                                                            @RequestParam("timeSlot") String timeSlot,
                                                            Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        TimeSlot slot = parseTimeSlot(timeSlot);
        LocalDate sessionDate = parseDate(date);
        Reservation reservation = bookingService.cancelReservation(sessionDate, slot, userId);
        String message = reservation.getStatus() == ReservationStatus.CANCELED
                ? "예약이 취소되었습니다."
                : "예약 상태가 변경되었습니다.";
        return ResponseEntity.ok(ReservationActionResponse.of(reservation, null, message));
    }

    @GetMapping("/me")
    public ResponseEntity<java.util.List<MyReservationResponse>> myReservations(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(
                bookingService.getReservationsForUser(userId)
                        .stream()
                        .map(MyReservationResponse::from)
                        .toList()
        );
    }

    private static TimeSlot parseTimeSlot(String timeSlot) {
        try {
            return TimeSlot.fromString(timeSlot);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid time slot");
        }
    }

    private static LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date");
        }
    }
}
