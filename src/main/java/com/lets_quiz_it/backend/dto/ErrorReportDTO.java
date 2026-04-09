package com.lets_quiz_it.backend.dto;

import lombok.Data;

@Data
public class ErrorReportDTO {
    private String qid;
    private String comment;
}