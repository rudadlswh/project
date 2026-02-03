package com.example.crossfit.config;

import com.example.crossfit.common.Role;
import com.example.crossfit.member.User;
import com.example.crossfit.member.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                userRepository.save(new User("admin@crossfit.local", passwordEncoder.encode("admin123"), Role.ADMIN, "관리자"));
                userRepository.save(new User("coach@crossfit.local", passwordEncoder.encode("coach123"), Role.COACH, "코치"));
                userRepository.save(new User("member@crossfit.local", passwordEncoder.encode("member123"), Role.MEMBER, "회원"));
            }
        };
    }
}
