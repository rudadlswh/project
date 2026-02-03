package com.example.crossfit.record;

import com.example.crossfit.member.User;
import com.example.crossfit.member.UserRepository;
import com.example.crossfit.wod.Wod;
import com.example.crossfit.wod.WodRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class WorkoutRecordService {
    private final WorkoutRecordRepository recordRepository;
    private final UserRepository userRepository;
    private final WodRepository wodRepository;

    public WorkoutRecordService(WorkoutRecordRepository recordRepository,
                                UserRepository userRepository,
                                WodRepository wodRepository) {
        this.recordRepository = recordRepository;
        this.userRepository = userRepository;
        this.wodRepository = wodRepository;
    }

    @Transactional
    public WorkoutRecord createRecord(Long userId, RecordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Wod wod = null;
        if (request.wodId() != null) {
            wod = wodRepository.findById(request.wodId())
                    .orElseThrow(() -> new EntityNotFoundException("Wod not found"));
        }
        RecordType recordType;
        try {
            recordType = RecordType.valueOf(request.type().toUpperCase());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid record type");
        }
        LocalDate recordDate = request.recordDate() != null ? request.recordDate() : LocalDate.now();
        WorkoutRecord record = new WorkoutRecord(user, wod, recordType, request.value(), request.imageUrl(), recordDate);
        return recordRepository.save(record);
    }

    public List<WorkoutRecord> getRecords(Long userId, YearMonth month) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return recordRepository.findByUserAndMonth(user, month.atDay(1), month.atEndOfMonth());
    }

    public List<WorkoutRecord> getRecordsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return recordRepository.findByUser(user);
    }

    @Transactional
    public void updateRecordImage(Long recordId, String imageUrl) {
        WorkoutRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException("Record not found"));
        record.setImageUrl(imageUrl);
    }

    @Transactional
    public BulkRecordResponse createBulkRecords(BulkRecordRequest request) {
        LocalDate recordDate = request.recordDate() != null ? request.recordDate() : LocalDate.now();
        RecordType recordType;
        try {
            recordType = RecordType.valueOf(request.recordType().toUpperCase(Locale.ROOT));
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid record type");
        }

        User creator = userRepository.findByEmailIgnoreCase("admin@crossfit.local")
                .orElseGet(() -> userRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new EntityNotFoundException("User not found")));
        Wod wod = wodRepository.findByWodDate(recordDate)
                .orElseGet(() -> wodRepository.save(new Wod(
                        recordDate,
                        request.wodTitle(),
                        "BULK",
                        "기록 일괄 등록",
                        creator
                )));

        List<String> failed = new ArrayList<>();
        int created = 0;
        for (String raw : request.members()) {
            if (raw == null || raw.trim().isEmpty()) {
                continue;
            }
            String token = raw.trim();
            User user = userRepository.findByEmailIgnoreCase(token)
                    .orElse(null);
            if (user == null) {
                var matches = userRepository.findByDisplayNameContainingIgnoreCase(token);
                if (matches.size() == 1) {
                    user = matches.get(0);
                } else {
                    failed.add(token);
                    continue;
                }
            }
            WorkoutRecord record = new WorkoutRecord(
                    user,
                    wod,
                    recordType,
                    request.value(),
                    null,
                    recordDate
            );
            recordRepository.save(record);
            created++;
        }
        return BulkRecordResponse.of(created, failed);
    }
}
