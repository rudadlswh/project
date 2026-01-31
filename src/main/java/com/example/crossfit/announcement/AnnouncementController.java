package com.example.crossfit.announcement;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {
    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @GetMapping
    public ResponseEntity<List<Announcement>> list() {
        return ResponseEntity.ok(announcementService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Announcement> detail(@PathVariable("id") Long id) {
        return ResponseEntity.ok(announcementService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Announcement> create(@Valid @RequestBody AnnouncementRequest request,
                                               Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(announcementService.create(request, userId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Announcement> update(@PathVariable("id") Long id,
                                               @Valid @RequestBody AnnouncementRequest request) {
        return ResponseEntity.ok(announcementService.update(id, request));
    }
}
