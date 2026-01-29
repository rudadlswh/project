package com.crossfit.web;

import com.crossfit.domain.User;
import com.crossfit.domain.Wod;
import com.crossfit.service.UserService;
import com.crossfit.service.WodService;
import com.crossfit.web.dto.WodDtos;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/wod")
public class WodController {
    private final WodService wodService;
    private final UserService userService;

    public WodController(WodService wodService, UserService userService) {
        this.wodService = wodService;
        this.userService = userService;
    }

    @GetMapping
    public WodDtos.WodResponse get(@RequestParam("date") LocalDate date) {
        Wod wod = wodService.getByDate(date);
        return toResponse(wod);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','COACH')")
    public WodDtos.WodResponse create(@Valid @RequestBody WodDtos.CreateWodRequest req) {
        User user = userService.getCurrentUser();
        Wod wod = wodService.createOrUpdate(user, req);
        return toResponse(wod);
    }

    private WodDtos.WodResponse toResponse(Wod wod) {
        WodDtos.WodResponse res = new WodDtos.WodResponse();
        res.id = wod.getId();
        res.date = wod.getDate();
        res.title = wod.getTitle();
        res.description = wod.getDescription();
        return res;
    }
}
