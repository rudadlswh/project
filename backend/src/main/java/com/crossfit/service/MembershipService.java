package com.crossfit.service;

import com.crossfit.domain.Membership;
import com.crossfit.domain.MembershipType;
import com.crossfit.domain.User;
import com.crossfit.repo.MembershipRepository;
import com.crossfit.web.dto.MembershipDtos;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class MembershipService {
    private final MembershipRepository membershipRepository;

    public MembershipService(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    @Transactional
    public Membership upsert(User user, MembershipDtos.UpsertMembershipRequest req) {
        Membership membership = membershipRepository.findByUser(user)
                .orElseGet(() -> new Membership(user, req.type, req.startDate, req.endDate, req.remainingCount));
        membership.setType(req.type);
        membership.setStartDate(req.startDate);
        membership.setEndDate(req.endDate);
        membership.setRemainingCount(req.remainingCount);
        return membershipRepository.save(membership);
    }

    public MembershipDtos.MembershipResponse get(User user) {
        Membership membership = membershipRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Membership not found"));
        MembershipDtos.MembershipResponse res = new MembershipDtos.MembershipResponse();
        res.type = membership.getType();
        res.startDate = membership.getStartDate();
        res.endDate = membership.getEndDate();
        res.remainingCount = membership.getRemainingCount();
        res.remainingDays = calculateRemainingDays(membership);
        return res;
    }

    private Integer calculateRemainingDays(Membership membership) {
        if (membership.getType() != MembershipType.PERIOD || membership.getEndDate() == null) {
            return null;
        }
        LocalDate today = LocalDate.now();
        if (today.isAfter(membership.getEndDate())) {
            return 0;
        }
        return (int) (membership.getEndDate().toEpochDay() - today.toEpochDay());
    }
}
