package com.crossfit.repo;

import com.crossfit.domain.Record;
import com.crossfit.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findByUserOrderByRecordDateDesc(User user);
    List<Record> findByUserAndRecordDate(User user, LocalDate date);
}
