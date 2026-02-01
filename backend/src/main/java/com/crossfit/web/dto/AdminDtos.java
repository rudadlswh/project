package com.crossfit.web.dto;

import com.crossfit.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AdminDtos {
    public static class UserSummary {
        public Long id;
        public String email;
        public String displayName;
        public Role role;
        public boolean active;
    }

    public static class UpdateRoleRequest {
        @NotNull
        public Role role;
        public Boolean active;
    }

    public static class CreateCoachRequest {
        @NotBlank
        @Email
        public String email;

        @NotBlank
        public String displayName;
    }

    public static class ExtendMembershipRequest {
        @NotBlank
        public String query;

        @NotNull
        @Min(1)
        public Integer days;
    }
}
