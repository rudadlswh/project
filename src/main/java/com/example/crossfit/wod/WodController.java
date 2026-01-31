package com.example.crossfit.wod;

import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wod")
public class WodController {
    private final WodService wodService;

    public WodController(WodService wodService) {
        this.wodService = wodService;
    }

    @GetMapping("/today")
    public ResponseEntity<Wod> getTodayWod(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(wodService.getTodayWod(date));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','COACH')")
    public ResponseEntity<Wod> createWod(@Valid @RequestBody WodRequest request,
                                         Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(wodService.createWod(request, userId));
    }
}
