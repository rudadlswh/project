package com.crossfit.service;

import com.crossfit.config.JwtTokenProvider;
import com.crossfit.domain.Role;
import com.crossfit.domain.User;
import com.crossfit.repo.UserRepository;
import com.crossfit.web.dto.AuthDtos;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest req) {
        if (userRepository.existsByEmail(req.email)) {
            throw new IllegalArgumentException("Email already in use");
        }
        User user = new User(req.email, passwordEncoder.encode(req.password), Role.MEMBER, req.displayName);
        userRepository.save(user);
        return issueToken(user);
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email, req.password)
        );
        User user = userRepository.findByEmail(req.email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return issueToken(user);
    }

    private AuthDtos.AuthResponse issueToken(User user) {
        AuthDtos.AuthResponse res = new AuthDtos.AuthResponse();
        res.accessToken = tokenProvider.createToken(user.getEmail(), user.getRole());
        res.role = user.getRole().name();
        res.displayName = user.getDisplayName();
        return res;
    }
}
