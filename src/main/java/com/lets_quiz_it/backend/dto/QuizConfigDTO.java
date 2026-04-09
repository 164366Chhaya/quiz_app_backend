package com.lets_quiz_it.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuizConfigDTO {
    private List<Long> topicIds;
    private int numberOfQuestions;    // 10, 25, 50, or custom
    private int timerPerQuestion;     // 45 or 60 seconds
    private boolean negativeMarking;
    private double negativeMarkRatio; // 0.25 or 0.50
}