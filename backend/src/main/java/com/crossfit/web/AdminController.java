package com.crossfit.web;

import com.crossfit.domain.Role;
import com.crossfit.domain.User;
import com.crossfit.repo.UserRepository;
import com.crossfit.service.MembershipService;
import com.crossfit.web.dto.AdminDtos;
import com.crossfit.web.dto.MembershipDtos;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasAnyRole('ADMIN','COACH')")
public class AdminController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MembershipService membershipService;

    public AdminController(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           MembershipService membershipService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.membershipService = membershipService;
    }

    @GetMapping("/users")
    public List<AdminDtos.UserSummary> listUsers() {
        return userRepository.findAll().stream().map(this::toSummary).collect(Collectors.toList());
    }

    @PatchMapping("/users/{id}")
    @org.springframework.transaction.annotation.Transactional
    public AdminDtos.UserSummary updateUser(@PathVariable Long id, @Valid @RequestBody AdminDtos.UpdateRoleRequest req) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setRole(req.role);
        if (req.active != null) {
            user.setActive(req.active);
        }
        return toSummary(user);
    }

    @PostMapping("/coaches")
    @PreAuthorize("hasRole('ADMIN')")
    public AdminDtos.UserSummary registerCoach(@Valid @RequestBody AdminDtos.CreateCoachRequest req) {
        if (userRepository.existsByEmail(req.email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
        User user = new User(req.email, passwordEncoder.encode("1234"), Role.COACH, req.displayName);
        userRepository.save(user);
        return toSummary(user);
    }

    @PostMapping("/memberships/extend")
    @PreAuthorize("hasRole('ADMIN')")
    public MembershipDtos.MembershipResponse extendMembership(@Valid @RequestBody AdminDtos.ExtendMembershipRequest req) {
        User user = findByQuery(req.query);
        membershipService.extendPeriod(user, req.days);
        return membershipService.get(user);
    }

    private AdminDtos.UserSummary toSummary(User user) {
        AdminDtos.UserSummary res = new AdminDtos.UserSummary();
        res.id = user.getId();
        res.email = user.getEmail();
        res.displayName = user.getDisplayName();
        res.role = user.getRole();
        res.active = user.isActive();
        return res;
    }

    private User findByQuery(String query) {
        String trimmed = query == null ? "" : query.trim();
        if (trimmed.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Query is required");
        }
        if (trimmed.contains("@")) {
            return userRepository.findByEmailIgnoreCase(trimmed)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        }
        List<User> users = userRepository.findByDisplayNameIgnoreCase(trimmed);
        if (users.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        if (users.size() > 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Multiple users found");
        }
        return users.get(0);
    }
}
