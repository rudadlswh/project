package com.crossfit.repo;

import com.crossfit.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailIgnoreCase(String email);
    List<User> findByDisplayNameIgnoreCase(String displayName);
    boolean existsByEmail(String email);
}
