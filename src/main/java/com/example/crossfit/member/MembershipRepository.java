package com.example.crossfit.member;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
    Optional<Membership> findTopByUserIdOrderByIdDesc(Long userId);
}
