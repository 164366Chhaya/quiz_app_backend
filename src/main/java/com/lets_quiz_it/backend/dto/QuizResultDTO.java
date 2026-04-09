package com.lets_quiz_it.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class QuizResultDTO {
    private Long sessionId;
    private int totalQuestions;
    private int correct;
    private int incorrect;
    private int skipped;
    private double score;
    private double accuracy;
    private long timeTakenSeconds;
    private boolean negativeMarking;
    private double negativeMarksDeducted;
    private String recommendation;
    private List<QuestionReviewDTO> questionReviews;

    @Data
    @Builder
    public static class QuestionReviewDTO {
        private String qid;
        private String questionText;
        private String optionA, optionB, optionC, optionD;
        private String correctOption;
        private String userAnswer;
        private String solutionText;
        private String hintText;
        private boolean correct;
        private boolean skipped;
    }
}