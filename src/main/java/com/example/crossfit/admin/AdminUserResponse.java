package com.example.crossfit.admin;

import com.example.crossfit.member.User;

public record AdminUserResponse(
        Long id,
        String email,
        String displayName,
        String role,
        boolean active
) {
    public static AdminUserResponse from(User user) {
        String displayName = user.getDisplayName() == null ? user.getEmail() : user.getDisplayName();
        boolean active = user.getActive() == null || user.getActive();
        return new AdminUserResponse(
                user.getId(),
                user.getEmail(),
                displayName,
                user.getRole().name(),
                active
        );
    }
}
