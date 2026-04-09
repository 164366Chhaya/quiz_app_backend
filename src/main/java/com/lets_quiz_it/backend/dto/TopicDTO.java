package com.lets_quiz_it.backend.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class TopicDTO {
    @NotBlank private String name;
    @NotBlank private String subject;
}