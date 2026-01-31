package com.example.crossfit.wod;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WodRepository extends JpaRepository<Wod, Long> {
    Optional<Wod> findByWodDate(LocalDate wodDate);
}
