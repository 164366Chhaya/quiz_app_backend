package com.lets_quiz_it.backend.service;

import com.lets_quiz_it.backend.entity.User;
import com.lets_quiz_it.backend.repository.UserRepository;
import com.lets_quiz_it.backend.repository.WhitelistedEmailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final WhitelistedEmailRepository whitelistedEmailRepository;

    @Value("${app.whitelisted-emails}")
    private String whitelistedEmails;

    @Value("${app.admin-email}")
    private String adminEmail;

    public boolean isWhitelisted(String email) {
        // Check DB first
        if (whitelistedEmailRepository.existsByEmailIgnoreCase(email)) return true;
        // Fallback to properties file
        List<String> allowed = Arrays.asList(whitelistedEmails.split(","));
        return allowed.stream().anyMatch(e -> e.trim().equalsIgnoreCase(email));
    }

    public User findOrCreateUser(String email, String name) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User.Role role = email.trim().equalsIgnoreCase(adminEmail.trim())
                    ? User.Role.ADMIN
                    : User.Role.USER;

            User newUser = User.builder()
                    .email(email)
                    .name(name)
                    .role(role)
                    .build();
            return userRepository.save(newUser);
        });
    }
}