package com.crossfit.config;

import com.crossfit.domain.Role;
import com.crossfit.domain.User;
import com.crossfit.repo.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements ApplicationRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;
    private final String adminDisplayName;
    private final boolean enabled;

    public AdminBootstrap(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          @Value("${app.admin.email:admin@crossfit.local}") String adminEmail,
                          @Value("${app.admin.password:admin1234}") String adminPassword,
                          @Value("${app.admin.display-name:관리자}") String adminDisplayName,
                          @Value("${app.admin.bootstrap:true}") boolean enabled) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
        this.adminDisplayName = adminDisplayName;
        this.enabled = enabled;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!enabled) {
            return;
        }
        userRepository.findByEmailIgnoreCase(adminEmail)
                .ifPresentOrElse(existing -> {
                    boolean changed = false;
                    if (existing.getRole() != Role.ADMIN) {
                        existing.setRole(Role.ADMIN);
                        changed = true;
                    }
                    if (!existing.isActive()) {
                        existing.setActive(true);
                        changed = true;
                    }
                    if (existing.getDisplayName() == null || existing.getDisplayName().isBlank()) {
                        existing.setDisplayName(adminDisplayName);
                        changed = true;
                    }
                    if (changed) {
                        userRepository.save(existing);
                    }
                }, () -> {
                    User user = new User(
                            adminEmail,
                            passwordEncoder.encode(adminPassword),
                            Role.ADMIN,
                            adminDisplayName
                    );
                    userRepository.save(user);
                });
    }
}
