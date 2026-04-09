package com.lets_quiz_it.backend.repository;

import com.lets_quiz_it.backend.entity.SessionQuestion;
import com.lets_quiz_it.backend.entity.QuizSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SessionQuestionRepository
        extends JpaRepository<SessionQuestion, Long> {

    List<SessionQuestion> findBySession(QuizSession session);
}