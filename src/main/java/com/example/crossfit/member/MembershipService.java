package com.example.crossfit.member;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElse(null);
        if (membership == null) {
            return new MembershipStatus(null, null, null, null, 0);
        }
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
}
