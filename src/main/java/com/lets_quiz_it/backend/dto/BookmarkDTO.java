package com.lets_quiz_it.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class BookmarkDTO {
    private Long bookmarkId;
    private String qid;
    private String questionText;
    private String optionA, optionB, optionC, optionD;
    private String correctOption;
    private String solutionText;
    private String hintText;
    private String topicName;
    private LocalDateTime bookmarkedAt;
}