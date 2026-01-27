package com.example.crossfit.member;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/memberships")
public class MembershipController {
    private final MembershipService membershipService;

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @GetMapping("/me")
    public ResponseEntity<MembershipStatus> getMyMembership(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(membershipService.getStatus(userId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','COACH')")
    public ResponseEntity<Membership> create(@Valid @RequestBody MembershipRequest request) {
        return ResponseEntity.ok(membershipService.createMembership(request));
    }
}
