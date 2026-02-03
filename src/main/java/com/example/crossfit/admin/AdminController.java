package com.example.crossfit.admin;

import com.example.crossfit.member.MembershipStatus;
import com.example.crossfit.member.MembershipService;
import com.example.crossfit.member.User;
import com.example.crossfit.member.UserRepository;
import com.example.crossfit.common.Role;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping({"/api/admin", "/admin"})
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

    @PostMapping("/coaches")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminUserResponse> createCoach(@Valid @RequestBody CreateCoachRequest request) {
        if (userRepository.findByEmailIgnoreCase(request.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        User user = new User(
                request.email(),
                passwordEncoder.encode("1234"),
                Role.COACH,
                request.displayName()
        );
        userRepository.save(user);
        return ResponseEntity.ok(AdminUserResponse.from(user));
    }

    @PostMapping("/memberships/extend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MembershipStatus> extendMembership(@Valid @RequestBody ExtendMembershipRequest request) {
        MembershipStatus status = membershipService.extendMembership(request.query(), request.days());
        return ResponseEntity.ok(status);
    }
}
