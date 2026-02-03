package com.example.crossfit.member;

public record UserResponse(
        Long id,
        String email,
        String displayName,
        String role,
        boolean active
) {
    public static UserResponse from(User user) {
        String displayName = user.getDisplayName() == null ? user.getEmail() : user.getDisplayName();
        boolean active = user.getActive() == null || user.getActive();
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                displayName,
                user.getRole().name(),
                active
        );
    }
}
