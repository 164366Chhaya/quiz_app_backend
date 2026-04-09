package com.lets_quiz_it.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class SessionSummaryDTO {
    private Long sessionId;
    private String topicIds;
    private int totalQuestions;
    private int correct;
    private int incorrect;
    private int skipped;
    private double score;
    private double accuracy;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
}