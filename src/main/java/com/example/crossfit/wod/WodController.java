package com.example.crossfit.wod;

import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/wod", "/wod"})
public class WodController {
    private final WodService wodService;

    public WodController(WodService wodService) {
        this.wodService = wodService;
    }

    @GetMapping
    public ResponseEntity<WodResponse> getByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Wod wod = wodService.getByDate(date);
        if (wod == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(WodResponse.from(wod));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','COACH')")
    public ResponseEntity<WodResponse> createWod(@Valid @RequestBody WodRequest request,
                                                 Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(WodResponse.from(wodService.createOrUpdate(request, userId)));
    }

    @DeleteMapping
    @PreAuthorize("hasAnyRole('ADMIN','COACH')")
    public ResponseEntity<Void> delete(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        wodService.deleteByDate(date);
        return ResponseEntity.noContent().build();
    }
}
