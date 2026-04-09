package com.lets_quiz_it.backend.repository;

import com.lets_quiz_it.backend.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    Optional<Topic> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}