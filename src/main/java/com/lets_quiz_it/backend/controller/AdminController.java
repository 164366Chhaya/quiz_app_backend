package com.lets_quiz_it.backend.controller;

import com.lets_quiz_it.backend.dto.*;
import com.lets_quiz_it.backend.entity.Topic;
import com.lets_quiz_it.backend.entity.WhitelistedEmail;
import com.lets_quiz_it.backend.repository.UserRepository;
import com.lets_quiz_it.backend.repository.WhitelistedEmailRepository;
import com.lets_quiz_it.backend.service.ErrorReportService;
import com.lets_quiz_it.backend.service.QuestionService;
import com.lets_quiz_it.backend.service.TopicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final TopicService topicService;
    private final UserRepository userRepository;
    private final QuestionService questionService;
    private final ErrorReportService errorReportService;
    private final WhitelistedEmailRepository whitelistedEmailRepository;

    // ── Topics ────────────────────────────────────────────────────

    @GetMapping("/topics")
    public ResponseEntity<List<Topic>> getAllTopics() {
        return ResponseEntity.ok(topicService.getAllTopics());
    }

    @PostMapping("/topics")
    public ResponseEntity<Topic> createTopic(@Valid @RequestBody TopicDTO dto) {
        return ResponseEntity.ok(topicService.createTopic(dto));
    }

    @PutMapping("/topics/{id}")
    public ResponseEntity<Topic> updateTopic(@PathVariable Long id,
                                             @Valid @RequestBody TopicDTO dto) {
        return ResponseEntity.ok(topicService.updateTopic(id, dto));
    }

    @DeleteMapping("/topics/{id}")
    public ResponseEntity<?> deleteTopic(@PathVariable Long id) {
        topicService.deleteTopic(id);
        return ResponseEntity.ok("Topic deleted");
    }

    // ── Questions ─────────────────────────────────────────────────

    @PostMapping("/questions/bulk-import")
    public ResponseEntity<BulkImportResultDTO> bulkImport(
            @RequestBody List<QuestionImportDTO> questions) {
        return ResponseEntity.ok(questionService.bulkImport(questions));
    }

    @GetMapping("/questions/topic/{topicId}")
    public ResponseEntity<?> getByTopic(@PathVariable Long topicId) {
        return ResponseEntity.ok(questionService.getByTopic(topicId));
    }

    @GetMapping("/questions/{question_id}")
    public ResponseEntity<?> getQuestion(@PathVariable String question_id) {
        return ResponseEntity.ok(questionService.getById(question_id));
    }

    @PutMapping("/questions/{question_id}")
    public ResponseEntity<?> updateQuestion(@PathVariable String question_id,
                                            @RequestBody QuestionImportDTO dto) {
        return ResponseEntity.ok(questionService.update(question_id, dto));
    }

    @DeleteMapping("/questions/{question_id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable String question_id) {
        questionService.delete(question_id);
        return ResponseEntity.ok("Question deleted");
    }

    // ── Error Reports (Admin) ─────────────────────────────────────

    @GetMapping("/error-reports")
    public ResponseEntity<?> getOpenReports() {
        return ResponseEntity.ok(errorReportService.getOpenReports());
    }

    @PutMapping("/error-reports/{id}/fix")
    public ResponseEntity<?> markFixed(@PathVariable Long id) {
        return ResponseEntity.ok(errorReportService.markFixed(id));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }
    // ── Whitelist Management ──────────────────────────────────

    @GetMapping("/whitelist")
    public ResponseEntity<?> getWhitelist() {
        return ResponseEntity.ok(whitelistedEmailRepository.findAll());
    }

    @PostMapping("/whitelist")
    public ResponseEntity<?> addToWhitelist(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank())
            return ResponseEntity.badRequest().body("Email is required");
        WhitelistedEmail entry = WhitelistedEmail.builder().email(email.trim()).build();
        whitelistedEmailRepository.save(entry);
        return ResponseEntity.ok("Added: " + email);
    }

    @DeleteMapping("/whitelist/{id}")
    public ResponseEntity<?> removeFromWhitelist(@PathVariable Long id) {
        whitelistedEmailRepository.deleteById(id);
        return ResponseEntity.ok("Removed from whitelist");
    }

}