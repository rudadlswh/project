package com.crossfit.web;

import com.crossfit.domain.TimeSlot;
import com.crossfit.domain.User;
import com.crossfit.service.ReservationService;
import com.crossfit.service.UserService;
import com.crossfit.web.dto.ReservationDtos;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationService reservationService;
    private final UserService userService;

    public ReservationController(ReservationService reservationService, UserService userService) {
        this.reservationService = reservationService;
        this.userService = userService;
    }

    @PostMapping
    public ReservationDtos.ReservationResponse reserve(@Valid @RequestBody ReservationDtos.ReserveRequest req) {
        User user = userService.getCurrentUser();
        return reservationService.reserve(user, req.date, req.timeSlot);
    }

    @DeleteMapping
    public ReservationDtos.ReservationResponse cancel(@RequestParam("date") LocalDate date,
                                                      @RequestParam("timeSlot") TimeSlot timeSlot) {
        User user = userService.getCurrentUser();
        return reservationService.cancel(user, date, timeSlot);
    }
}
