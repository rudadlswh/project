package com.example.crossfit.record;

import jakarta.validation.Valid;
import java.time.YearMonth;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/records")
public class WorkoutRecordController {
    private final WorkoutRecordService recordService;

    public WorkoutRecordController(WorkoutRecordService recordService) {
        this.recordService = recordService;
    }

    @PostMapping
    public ResponseEntity<WorkoutRecord> create(@Valid @RequestBody RecordRequest request,
                                                Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(recordService.createRecord(userId, request));
    }

    @GetMapping
    public ResponseEntity<java.util.List<WorkoutRecord>> history(@RequestParam("month") String month,
                                                                 Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(recordService.getRecords(userId, YearMonth.parse(month)));
    }
}
