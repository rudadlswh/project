package com.crossfit.web;

import com.crossfit.domain.User;
import com.crossfit.repo.UserRepository;
import com.crossfit.web.dto.AdminDtos;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasAnyRole('ADMIN','COACH')")
public class AdminController {
    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    private AdminDtos.UserSummary toSummary(User user) {
        AdminDtos.UserSummary res = new AdminDtos.UserSummary();
        res.id = user.getId();
        res.email = user.getEmail();
        res.displayName = user.getDisplayName();
        res.role = user.getRole();
        res.active = user.isActive();
        return res;
    }
}
