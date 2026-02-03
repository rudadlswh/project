package com.example.crossfit.record;

import com.example.crossfit.member.User;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorkoutRecordRepository extends JpaRepository<WorkoutRecord, Long> {
    @Query("select r from WorkoutRecord r where r.user = :user and r.recordDate between :start and :end")
    List<WorkoutRecord> findByUserAndMonth(@Param("user") User user,
                                           @Param("start") LocalDate start,
                                           @Param("end") LocalDate end);

    List<WorkoutRecord> findByUser(User user);
}
