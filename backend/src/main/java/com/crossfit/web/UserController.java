package com.crossfit.web;

import com.crossfit.domain.User;
import com.crossfit.service.UserService;
import com.crossfit.web.dto.UserDtos;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserDtos.UserResponse me() {
        User user = userService.getCurrentUser();
        UserDtos.UserResponse res = new UserDtos.UserResponse();
        res.id = user.getId();
        res.email = user.getEmail();
        res.displayName = user.getDisplayName();
        res.role = user.getRole().name();
        res.active = user.isActive();
        return res;
    }
}
