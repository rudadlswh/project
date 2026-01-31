package com.crossfit.web;

import com.crossfit.domain.User;
import com.crossfit.service.MembershipService;
import com.crossfit.service.UserService;
import com.crossfit.web.dto.MembershipDtos;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/memberships")
public class MembershipController {
    private final MembershipService membershipService;
    private final UserService userService;

    public MembershipController(MembershipService membershipService, UserService userService) {
        this.membershipService = membershipService;
        this.userService = userService;
    }

    @GetMapping("/me")
    public MembershipDtos.MembershipResponse my() {
        User user = userService.getCurrentUser();
        return membershipService.get(user);
    }

    @PostMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','COACH')")
    public MembershipDtos.MembershipResponse upsert(@PathVariable Long userId,
                                                    @Valid @RequestBody MembershipDtos.UpsertMembershipRequest req) {
        User user = userService.requireById(userId);
        membershipService.upsert(user, req);
        return membershipService.get(user);
    }
}
