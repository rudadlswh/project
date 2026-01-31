package com.crossfit.service;

import com.crossfit.domain.Record;
import com.crossfit.domain.User;
import com.crossfit.domain.Wod;
import com.crossfit.repo.RecordRepository;
import com.crossfit.repo.WodRepository;
import com.crossfit.web.dto.RecordDtos;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecordService {
    private final RecordRepository recordRepository;
    private final WodRepository wodRepository;

    public RecordService(RecordRepository recordRepository, WodRepository wodRepository) {
        this.recordRepository = recordRepository;
        this.wodRepository = wodRepository;
    }

    @Transactional
    public Record create(User user, RecordDtos.CreateRecordRequest req) {
        Wod wod = null;
        if (req.wodId != null) {
            wod = wodRepository.findById(req.wodId)
                    .orElseThrow(() -> new IllegalArgumentException("WOD not found"));
        }
        Record record = new Record(user, wod, req.type, req.value, req.recordDate);
        return recordRepository.save(record);
    }

    public List<Record> listByUser(User user) {
        return recordRepository.findByUserOrderByRecordDateDesc(user);
    }
}
