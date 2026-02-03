package com.example.crossfit.auth;

import com.example.crossfit.member.User;
import com.example.crossfit.member.UserRepository;
import com.example.crossfit.common.Role;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        String token = jwtTokenProvider.createToken(user.getId(), user.getRole().name());
        String displayName = user.getDisplayName() == null ? user.getEmail() : user.getDisplayName();
        return new LoginResponse(token, user.getRole().name(), displayName);
    }

    public LoginResponse register(RegisterRequest request) {
        if (userRepository.findByEmailIgnoreCase(request.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        User user = new User(
                request.email(),
                passwordEncoder.encode(request.password()),
                Role.MEMBER,
                request.displayName());
        userRepository.save(user);
        String token = jwtTokenProvider.createToken(user.getId(), user.getRole().name());
        String displayName = user.getDisplayName() == null ? user.getEmail() : user.getDisplayName();
        return new LoginResponse(token, user.getRole().name(), displayName);
    }
}
