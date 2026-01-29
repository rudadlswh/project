package com.crossfit.service;

import com.crossfit.domain.User;
import com.crossfit.domain.Wod;
import com.crossfit.repo.WodRepository;
import com.crossfit.web.dto.WodDtos;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class WodService {
    private final WodRepository wodRepository;

    public WodService(WodRepository wodRepository) {
        this.wodRepository = wodRepository;
    }

    @Transactional
    public Wod createOrUpdate(User creator, WodDtos.CreateWodRequest req) {
        Wod wod = wodRepository.findByDate(req.date)
                .orElseGet(() -> new Wod(req.date, req.title, req.description, creator));
        wod.setTitle(req.title);
        wod.setDescription(req.description);
        return wodRepository.save(wod);
    }

    public Wod getByDate(LocalDate date) {
        return wodRepository.findByDate(date)
                .orElseThrow(() -> new IllegalArgumentException("WOD not found"));
    }
}
