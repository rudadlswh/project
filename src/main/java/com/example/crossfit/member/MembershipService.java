package com.example.crossfit.member;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class MembershipService {
    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;

    public MembershipService(MembershipRepository membershipRepository, UserRepository userRepository) {
        this.membershipRepository = membershipRepository;
        this.userRepository = userRepository;
    }

    public MembershipStatus getStatus(Long userId) {
        Membership membership = membershipRepository.findTopByUserIdOrderByIdDesc(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Membership not found"));
        long remainingDays = 0;
        if (membership.getType() == MembershipType.PERIOD && membership.getEndDate() != null) {
            remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), membership.getEndDate());
            remainingDays = Math.max(remainingDays, 0);
        }
        return new MembershipStatus(
                membership.getType(),
                membership.getStartDate(),
                membership.getEndDate(),
                membership.getRemainingCount(),
                remainingDays
        );
    }

    @Transactional
    public Membership createMembership(MembershipRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Membership membership = new Membership(
                user,
                request.type(),
                request.startDate(),
                request.endDate(),
                request.remainingCount()
        );
        return membershipRepository.save(membership);
    }

    public Membership getLatestMembership(Long userId) {
        return membershipRepository.findTopByUserIdOrderByIdDesc(userId)
                .orElse(null);
    }

    public Membership ensureActiveMembershipForReservation(Long userId, LocalDate sessionDate) {
        Membership membership = membershipRepository.findTopByUserIdOrderByIdDesc(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Membership required"));
        if (membership.getType() == MembershipType.PERIOD) {
            if (membership.getStartDate() != null && sessionDate.isBefore(membership.getStartDate())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Membership not active yet");
            }
            if (membership.getEndDate() != null && sessionDate.isAfter(membership.getEndDate())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Membership expired");
            }
            return membership;
        }
        Integer remaining = membership.getRemainingCount();
        if (remaining == null || remaining <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No remaining sessions");
        }
        return membership;
    }

    @Transactional
    public void consumeIfCount(Membership membership) {
        if (membership == null) {
            return;
        }
        if (membership.getType() == MembershipType.COUNT) {
            Integer remaining = membership.getRemainingCount();
            if (remaining == null || remaining <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No remaining sessions");
            }
            membership.setRemainingCount(remaining - 1);
        }
    }

    @Transactional
    public void refundIfCount(Membership membership) {
        if (membership == null) {
            return;
        }
        if (membership.getType() == MembershipType.COUNT) {
            Integer remaining = membership.getRemainingCount();
            membership.setRemainingCount((remaining == null ? 0 : remaining) + 1);
        }
    }

    @Transactional
    public MembershipStatus extendMembership(String query, int days) {
        User user = userRepository.findByEmailIgnoreCase(query)
                .orElse(null);
        if (user == null) {
            var matches = userRepository.findByDisplayNameContainingIgnoreCase(query);
            if (matches.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            }
            if (matches.size() > 1) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Multiple users found");
            }
            user = matches.get(0);
        }
        Membership latest = membershipRepository.findTopByUserIdOrderByIdDesc(user.getId())
                .orElse(null);
        MembershipType type = latest != null ? latest.getType() : MembershipType.PERIOD;
        LocalDate startDate = latest != null ? latest.getStartDate() : LocalDate.now();
        LocalDate endDate = latest != null ? latest.getEndDate() : LocalDate.now();
        Integer remainingCount = latest != null ? latest.getRemainingCount() : null;
        if (type == MembershipType.PERIOD) {
            LocalDate base = endDate != null ? endDate : LocalDate.now();
            endDate = base.plusDays(days);
        } else {
            int base = remainingCount == null ? 0 : remainingCount;
            remainingCount = base + days;
        }
        Membership membership = new Membership(user, type, startDate, endDate, remainingCount);
        membershipRepository.save(membership);
        long remainingDays = 0;
        if (type == MembershipType.PERIOD && endDate != null) {
            remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), endDate);
            remainingDays = Math.max(remainingDays, 0);
        }
        return new MembershipStatus(type, startDate, endDate, remainingCount, remainingDays);
    }
}
