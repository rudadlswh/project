package com.crossfit.web.dto;

public class UserDtos {
    public static class UserResponse {
        public Long id;
        public String email;
        public String displayName;
        public String role;
        public boolean active;
    }
}
