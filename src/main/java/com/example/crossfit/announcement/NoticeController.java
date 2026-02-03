package com.example.crossfit.announcement;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/notices", "/notices"})
public class NoticeController {
    private final AnnouncementService announcementService;

    public NoticeController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @GetMapping
    public ResponseEntity<List<NoticeResponse>> list() {
        List<NoticeResponse> responses = announcementService.getAll().stream()
                .map(NoticeResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticeResponse> detail(@PathVariable("id") Long id) {
        return ResponseEntity.ok(NoticeResponse.from(announcementService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NoticeResponse> create(@Valid @RequestBody NoticeRequest request,
                                                 Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        Announcement created = announcementService.create(new AnnouncementRequest(request.title(), request.content()), userId);
        return ResponseEntity.ok(NoticeResponse.from(created));
    }
}
