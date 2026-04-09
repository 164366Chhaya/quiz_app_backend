package com.lets_quiz_it.backend.controller;

import com.lets_quiz_it.backend.dto.*;
import com.lets_quiz_it.backend.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final BookmarkService bookmarkService;
    private final ErrorReportService errorReportService;
    private final SessionService sessionService;

    // ── Bookmarks ─────────────────────────────────────────────────

    @GetMapping("/bookmarks")
    public ResponseEntity<List<BookmarkDTO>> getBookmarks(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(bookmarkService.getBookmarks(email));
    }
    @PostMapping("/bookmarks/{question_id}")
    public ResponseEntity<String> toggleBookmark(
            @AuthenticationPrincipal String email,
            @PathVariable String question_id) {
        return ResponseEntity.ok(bookmarkService.toggleBookmark(email, question_id));
    }

    // ── Error Reports ─────────────────────────────────────────────

    @PostMapping("/error-report")
    public ResponseEntity<String> reportError(
            @AuthenticationPrincipal String email,
            @RequestBody ErrorReportDTO dto) {
        return ResponseEntity.ok(errorReportService.reportError(email, dto));
    }

    // ── Sessions ──────────────────────────────────────────────────

    @GetMapping("/sessions")
    public ResponseEntity<List<SessionSummaryDTO>> getSessions(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(sessionService.getUserSessions(email));
    }


}