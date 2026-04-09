package com.lets_quiz_it.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_sessions")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class QuizSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "topic_ids", nullable = false)
    private String topicIds;          // stored as "1,2,3"

    @Column(name = "total_questions") private int totalQuestions;
    @Column private int correct;
    @Column private int incorrect;
    @Column private int skipped;
    @Column private double score;

    @Column(name = "negative_marks_applied")
    private boolean negativeMarksApplied;

    @Column(name = "started_at")  private LocalDateTime startedAt;
    @Column(name = "ended_at")    private LocalDateTime endedAt;
}
