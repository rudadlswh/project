package com.crossfit.web;

import com.crossfit.domain.Record;
import com.crossfit.domain.User;
import com.crossfit.service.RecordService;
import com.crossfit.service.UserService;
import com.crossfit.web.dto.RecordDtos;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/records")
public class RecordController {
    private final RecordService recordService;
    private final UserService userService;

    public RecordController(RecordService recordService, UserService userService) {
        this.recordService = recordService;
        this.userService = userService;
    }

    @PostMapping
    public RecordDtos.RecordResponse create(@Valid @RequestBody RecordDtos.CreateRecordRequest req) {
        User user = userService.getCurrentUser();
        return toResponse(recordService.create(user, req));
    }

    @GetMapping("/my")
    public List<RecordDtos.RecordResponse> my() {
        User user = userService.getCurrentUser();
        List<Record> records = recordService.listByUser(user);
        return records.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private RecordDtos.RecordResponse toResponse(Record record) {
        RecordDtos.RecordResponse res = new RecordDtos.RecordResponse();
        res.id = record.getId();
        res.wodId = record.getWod() == null ? null : record.getWod().getId();
        res.type = record.getType();
        res.value = record.getValue();
        res.recordDate = record.getRecordDate();
        return res;
    }
}
