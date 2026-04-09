package com.lets_quiz_it.backend.service;

import com.lets_quiz_it.backend.dto.SessionSummaryDTO;
import com.lets_quiz_it.backend.entity.*;
import com.lets_quiz_it.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final QuizSessionRepository quizSessionRepository;
    private final UserRepository userRepository;

    public List<SessionSummaryDTO> getUserSessions(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return quizSessionRepository.findByUserOrderByStartedAtDesc(user)
                .stream()
                .map(s -> {
                    double accuracy = s.getTotalQuestions() == 0 ? 0 :
                            (s.getCorrect() * 100.0) / s.getTotalQuestions();

                    return SessionSummaryDTO.builder()
                            .sessionId(s.getId())
                            .topicIds(s.getTopicIds())
                            .totalQuestions(s.getTotalQuestions())
                            .correct(s.getCorrect())
                            .incorrect(s.getIncorrect())
                            .skipped(s.getSkipped())
                            .score(s.getScore())
                            .accuracy(accuracy)
                            .startedAt(s.getStartedAt())
                            .endedAt(s.getEndedAt())
                            .build();
                }).toList();
    }
}