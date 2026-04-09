package com.lets_quiz_it.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class QuizStartResponseDTO {
    private Long sessionId;
    private List<QuestionDTO> questions;
    private int timerPerQuestion;
    private boolean negativeMarking;
    private double negativeMarkRatio;
}