package com.lets_quiz_it.backend.controller;

import com.lets_quiz_it.backend.dto.*;
import com.lets_quiz_it.backend.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @PostMapping("/start")
    public ResponseEntity<QuizStartResponseDTO> startQuiz(
            @AuthenticationPrincipal String email,
            @RequestBody QuizConfigDTO config) {
        return ResponseEntity.ok(quizService.startQuiz(email, config));
    }

    @PostMapping("/submit")
    public ResponseEntity<QuizResultDTO> submitQuiz(
            @AuthenticationPrincipal String email,
            @RequestBody QuizSubmitDTO submitDTO) {
        return ResponseEntity.ok(quizService.submitQuiz(email, submitDTO));
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<QuizResultDTO> getSession(
            @PathVariable Long sessionId) {
        return ResponseEntity.ok(quizService.getSessionResult(sessionId));
    }
}