package com.example.crossfit.announcement;

import com.example.crossfit.member.User;
import com.example.crossfit.member.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnnouncementService {
    private final AnnouncementRepository repository;
    private final UserRepository userRepository;

    public AnnouncementService(AnnouncementRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public List<Announcement> getAll() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    public Announcement getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Announcement not found"));
    }

    @Transactional
    public Announcement create(AnnouncementRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return repository.save(new Announcement(request.title(), request.body(), user));
    }

    @Transactional
    public Announcement update(Long id, AnnouncementRequest request) {
        Announcement announcement = getById(id);
        announcement.update(request.title(), request.body());
        return announcement;
    }
}
