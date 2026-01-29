package com.crossfit.web.dto;

import com.crossfit.domain.Role;
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
}
