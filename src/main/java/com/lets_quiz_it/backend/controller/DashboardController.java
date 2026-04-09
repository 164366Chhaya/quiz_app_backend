package com.lets_quiz_it.backend.controller;

import com.lets_quiz_it.backend.entity.QuizSession;
import com.lets_quiz_it.backend.entity.User;
import com.lets_quiz_it.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/user/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final QuizSessionRepository sessionRepository;
    private final SessionQuestionRepository sessionQuestionRepository;
    private final UserRepository userRepository;
    private final UserTopicProgressRepository progressRepository;
    private final QuestionRepository questionRepository;

    @GetMapping
    public ResponseEntity<?> getDashboard(@AuthenticationPrincipal String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        List<QuizSession> allSessions = sessionRepository.findByUserOrderByStartedAtDesc(user);

        // Today's sessions
        LocalDate today = LocalDate.now();
        List<QuizSession> todaySessions = allSessions.stream()
                .filter(s -> s.getStartedAt().toLocalDate().equals(today))
                .toList();

        double todayAccuracy = calcAccuracy(todaySessions);
        double overallAccuracy = calcAccuracy(allSessions);

        // Streak
        int streak = calcStreak(allSessions);

        // Per-topic performance
        // Cycle progress per topic
        List<Map<String, Object>> topicStats = progressRepository.findByUser(user).stream()
                .map(p -> {
                    long total = questionRepository.countByTopic(p.getTopic());
                    Map<String, Object> stat = new HashMap<>();
                    stat.put("topicName", p.getTopic().getName());
                    stat.put("subject", p.getTopic().getSubject());
                    stat.put("currentPointer", p.getCurrentPointer());
                    stat.put("totalQuestions", total);
                    stat.put("cycleCount", p.getCycleCount());
                    return stat;
                })
                .toList();
        return ResponseEntity.ok(Map.of(
                "todayAccuracy", todayAccuracy,
                "overallAccuracy", overallAccuracy,
                "totalSessions", allSessions.size(),
                "streak", streak,
                "topicStats", topicStats
        ));
    }

    private double calcAccuracy(List<QuizSession> sessions) {
        int totalQ = sessions.stream().mapToInt(QuizSession::getTotalQuestions).sum();
        int totalCorrect = sessions.stream().mapToInt(QuizSession::getCorrect).sum();
        return totalQ == 0 ? 0 : (totalCorrect * 100.0) / totalQ;
    }

    private int calcStreak(List<QuizSession> sessions) {
        if (sessions.isEmpty()) return 0;
        int streak = 0;
        LocalDate check = LocalDate.now();
        Set<LocalDate> sessionDates = sessions.stream()
                .map(s -> s.getStartedAt().toLocalDate())
                .collect(java.util.stream.Collectors.toSet());

        while (sessionDates.contains(check)) {
            streak++;
            check = check.minusDays(1);
        }
        return streak;
    }
}