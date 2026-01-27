package com.example.crossfit.wod;

import com.example.crossfit.member.User;
import com.example.crossfit.member.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WodService {
    private final WodRepository wodRepository;
    private final UserRepository userRepository;

    public WodService(WodRepository wodRepository, UserRepository userRepository) {
        this.wodRepository = wodRepository;
        this.userRepository = userRepository;
    }

    public Wod getTodayWod(LocalDate date) {
        return wodRepository.findByWodDate(date).orElse(null);
    }

    @Transactional
    public Wod createWod(WodRequest request, Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Wod wod = new Wod(request.date(), request.title(), request.description(), creator);
        return wodRepository.save(wod);
    }
}
