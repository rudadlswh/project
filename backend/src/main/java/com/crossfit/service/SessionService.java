package com.crossfit.service;

import com.crossfit.domain.ClassSession;
import com.crossfit.domain.TimeSlot;
import com.crossfit.repo.ClassSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Service
public class SessionService {
    private final ClassSessionRepository sessionRepository;

    public SessionService(ClassSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    public List<ClassSession> ensureDailySessions(LocalDate date) {
        List<ClassSession> existing = sessionRepository.findBySessionDate(date);
        if (existing.size() == TimeSlot.values().length) {
            return existing;
        }
        EnumSet<TimeSlot> present = EnumSet.noneOf(TimeSlot.class);
        for (ClassSession session : existing) {
            present.add(session.getTimeSlot());
        }
        List<ClassSession> created = new ArrayList<>(existing);
        for (TimeSlot slot : TimeSlot.values()) {
            if (!present.contains(slot)) {
                created.add(sessionRepository.save(new ClassSession(date, slot, null)));
            }
        }
        return created;
    }

    public ClassSession requireSession(LocalDate date, TimeSlot slot) {
        return sessionRepository.findBySessionDateAndTimeSlot(date, slot)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));
    }

    @Transactional
    public ClassSession updateCapacity(Long sessionId, Integer capacity) {
        ClassSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));
        session.setCapacity(capacity);
        return session;
    }
}
