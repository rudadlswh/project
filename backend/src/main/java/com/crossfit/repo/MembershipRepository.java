package com.crossfit.repo;

import com.crossfit.domain.Membership;
import com.crossfit.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
    Optional<Membership> findByUser(User user);
}
