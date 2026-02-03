package com.example.crossfit.wod;

import com.example.crossfit.member.User;
import com.example.crossfit.member.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class WodService {
    private final WodRepository wodRepository;
    private final UserRepository userRepository;

    public WodService(WodRepository wodRepository, UserRepository userRepository) {
        this.wodRepository = wodRepository;
        this.userRepository = userRepository;
    }

    public Wod getByDate(LocalDate date) {
        return wodRepository.findByWodDate(date).orElse(null);
    }

    @Transactional
    public Wod createOrUpdate(WodRequest request, Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return wodRepository.findByWodDate(request.date())
                .map(existing -> {
                    existing.update(request.title(), request.type(), request.description());
                    return existing;
                })
                .orElseGet(() -> wodRepository.save(
                        new Wod(request.date(), request.title(), request.type(), request.description(), creator)));
    }

    @Transactional
    public void deleteByDate(LocalDate date) {
        Wod wod = wodRepository.findByWodDate(date)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wod not found"));
        wodRepository.delete(wod);
    }
}
