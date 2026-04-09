package com.lets_quiz_it.backend.controller;

import com.lets_quiz_it.backend.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    @GetMapping
    public ResponseEntity<?> getAllTopics() {
        return ResponseEntity.ok(topicService.getAllTopics());
    }
}