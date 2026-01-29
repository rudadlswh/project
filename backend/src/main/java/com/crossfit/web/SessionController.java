package com.crossfit.web;

import com.crossfit.domain.ClassSession;
import com.crossfit.domain.Reservation;
import com.crossfit.domain.ReservationStatus;
import com.crossfit.domain.User;
import com.crossfit.repo.ReservationRepository;
import com.crossfit.service.SessionService;
import com.crossfit.service.UserService;
import com.crossfit.web.dto.SessionDtos;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sessions")
public class SessionController {
    private final SessionService sessionService;
    private final ReservationRepository reservationRepository;
    private final UserService userService;

    public SessionController(SessionService sessionService,
                             ReservationRepository reservationRepository,
                             UserService userService) {
        this.sessionService = sessionService;
        this.reservationRepository = reservationRepository;
        this.userService = userService;
    }

    @GetMapping
    public List<SessionDtos.SessionResponse> list(@RequestParam("date") LocalDate date) {
        User user = userService.getCurrentUser();
        List<ClassSession> sessions = sessionService.ensureDailySessions(date);
        return sessions.stream()
                .sorted(Comparator.comparing(ClassSession::getTimeSlot))
                .map(session -> toResponse(session, user))
                .collect(Collectors.toList());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','COACH')")
    public SessionDtos.SessionResponse create(@Valid @RequestBody SessionDtos.CreateSessionRequest req) {
        sessionService.ensureDailySessions(req.date);
        ClassSession session = sessionService.requireSession(req.date, req.timeSlot);
        session = sessionService.updateCapacity(session.getId(), req.capacity);
        return toResponse(session, userService.getCurrentUser());
    }

    @PatchMapping("/{id}/capacity")
    @PreAuthorize("hasAnyRole('ADMIN','COACH')")
    public SessionDtos.SessionResponse updateCapacity(@PathVariable Long id,
                                                      @RequestBody SessionDtos.UpdateCapacityRequest req) {
        ClassSession session = sessionService.updateCapacity(id, req.capacity);
        return toResponse(session, userService.getCurrentUser());
    }

    private SessionDtos.SessionResponse toResponse(ClassSession session, User user) {
        SessionDtos.SessionResponse res = new SessionDtos.SessionResponse();
        res.id = session.getId();
        res.date = session.getSessionDate();
        res.timeSlot = session.getTimeSlot();
        res.capacity = session.getCapacity();
        res.bookedCount = reservationRepository.countBySessionAndStatus(session, ReservationStatus.BOOKED);
        res.waitlistCount = reservationRepository.countBySessionAndStatus(session, ReservationStatus.WAITLISTED);
        Reservation my = reservationRepository.findByUserAndSession(user, session).orElse(null);
        if (my != null && my.getStatus() != ReservationStatus.CANCELLED) {
            res.myStatus = my.getStatus();
            res.myWaitlistPosition = my.getWaitlistPosition();
        }
        return res;
    }
}
