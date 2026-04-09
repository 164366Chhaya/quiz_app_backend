package com.lets_quiz_it.backend.repository;

import com.lets_quiz_it.backend.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByUser(User user);
    Optional<Bookmark> findByUserAndQuestion(User user, Question question);
    boolean existsByUserAndQuestion(User user, Question question);
    void deleteByUserAndQuestion(User user, Question question);
}