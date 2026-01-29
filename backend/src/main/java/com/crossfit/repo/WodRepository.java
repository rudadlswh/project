package com.crossfit.repo;

import com.crossfit.domain.Wod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface WodRepository extends JpaRepository<Wod, Long> {
    Optional<Wod> findByDate(LocalDate date);
}
