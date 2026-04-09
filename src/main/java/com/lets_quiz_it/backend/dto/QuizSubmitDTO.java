package com.lets_quiz_it.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuizSubmitDTO {
    private Long sessionId;
    private double negativeMarkRatio;
    private List<QuestionAnswerDTO> answers;

    @Data
    public static class QuestionAnswerDTO {
        private String qid;
        private String userAnswer;   // "A", "B", "C", "D", or null if skipped
        private int timeTakenSeconds;
    }
}