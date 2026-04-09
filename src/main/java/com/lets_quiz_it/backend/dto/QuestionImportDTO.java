package com.lets_quiz_it.backend.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Data
public class QuestionImportDTO {
    @NotBlank private String qid;
    @NotBlank private String topic;       // topic name e.g. "Percentage"
    @NotBlank private String question_text;
    @NotBlank private String option_a;
    @NotBlank private String option_b;
    @NotBlank private String option_c;
    @NotBlank private String option_d;
    @NotBlank private String correct_option;
    private String solution_text;
    private String hint_text;
    private String image_url;
    private List<String> topics;

}