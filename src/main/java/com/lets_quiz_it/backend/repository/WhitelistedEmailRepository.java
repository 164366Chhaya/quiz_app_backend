package com.lets_quiz_it.backend.repository;

import com.lets_quiz_it.backend.entity.WhitelistedEmail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WhitelistedEmailRepository extends JpaRepository<WhitelistedEmail, Long> {
    boolean existsByEmailIgnoreCase(String email);
}
