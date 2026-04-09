package com.lets_quiz_it.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionDTO {
    private String qid;
    private String topicName;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String hintText;
    // ⚠️ correctOption and solutionText are NOT included here
}