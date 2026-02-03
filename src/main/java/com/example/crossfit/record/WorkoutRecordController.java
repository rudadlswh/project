package com.example.crossfit.record;

import jakarta.validation.Valid;
import java.time.YearMonth;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/records", "/records"})
public class WorkoutRecordController {
    private final WorkoutRecordService recordService;

    public WorkoutRecordController(WorkoutRecordService recordService) {
        this.recordService = recordService;
    }

    @PostMapping
    public ResponseEntity<RecordResponse> create(@Valid @RequestBody RecordRequest request,
                                                 Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(RecordResponse.from(recordService.createRecord(userId, request)));
    }

    @GetMapping
    public ResponseEntity<java.util.List<RecordResponse>> history(@RequestParam("month") String month,
                                                                  Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(
                recordService.getRecords(userId, YearMonth.parse(month))
                        .stream()
                        .map(RecordResponse::from)
                        .toList());
    }

    @GetMapping("/my")
    public ResponseEntity<java.util.List<RecordResponse>> myRecords(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(
                recordService.getRecordsForUser(userId)
                        .stream()
                        .map(RecordResponse::from)
                        .toList());
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('ADMIN','COACH')")
    public ResponseEntity<BulkRecordResponse> bulkCreate(@Valid @RequestBody BulkRecordRequest request) {
        return ResponseEntity.ok(recordService.createBulkRecords(request));
    }
}
