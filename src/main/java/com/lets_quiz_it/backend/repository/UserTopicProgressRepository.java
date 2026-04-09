package com.lets_quiz_it.backend.repository;

import com.lets_quiz_it.backend.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserTopicProgressRepository
        extends JpaRepository<UserTopicProgress, Long> {

    Optional<UserTopicProgress> findByUserAndTopic(User user, Topic topic);
    List<UserTopicProgress> findByUser(User user);  // ← add this line
}