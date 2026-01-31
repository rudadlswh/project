package com.example.crossfit.record;

import com.example.crossfit.member.User;
import com.example.crossfit.member.UserRepository;
import com.example.crossfit.wod.Wod;
import com.example.crossfit.wod.WodRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.YearMonth;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Wod wod = wodRepository.findById(request.wodId())
                .orElseThrow(() -> new EntityNotFoundException("Wod not found"));
        WorkoutRecord record = new WorkoutRecord(user, wod, request.type(), request.value());
        return recordRepository.save(record);
    }

    public List<WorkoutRecord> getRecords(Long userId, YearMonth month) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return recordRepository.findByUserAndMonth(user, month.atDay(1), month.atEndOfMonth());
    }
}
