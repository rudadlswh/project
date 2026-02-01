package com.crossfit.service;

import com.crossfit.domain.Membership;
import com.crossfit.domain.MembershipType;
import com.crossfit.domain.User;
import com.crossfit.repo.MembershipRepository;
import com.crossfit.web.dto.MembershipDtos;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Membership not found"));
        MembershipDtos.MembershipResponse res = new MembershipDtos.MembershipResponse();
        res.type = membership.getType();
        res.startDate = membership.getStartDate();
        res.endDate = membership.getEndDate();
        res.remainingCount = membership.getRemainingCount();
        res.remainingDays = calculateRemainingDays(membership);
        return res;
    }

    @Transactional
    public Membership extendPeriod(User user, int days) {
        if (days <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Days must be positive");
        }
        LocalDate today = LocalDate.now();
        Membership membership = membershipRepository.findByUser(user)
                .orElseGet(() -> new Membership(user, MembershipType.PERIOD, today, today.plusDays(days), null));
        if (membership.getType() != MembershipType.PERIOD) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Membership type must be PERIOD");
        }
        LocalDate base = membership.getEndDate();
        if (base == null || base.isBefore(today)) {
            base = today;
        }
        membership.setEndDate(base.plusDays(days));
        if (membership.getStartDate() == null) {
            membership.setStartDate(today);
        }
        return membershipRepository.save(membership);
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
