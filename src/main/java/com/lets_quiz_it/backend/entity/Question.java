package com.lets_quiz_it.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    private String qid;           // e.g. "PCT-0042"

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "question_topics",
        joinColumns = @JoinColumn(name = "question_id"),
        inverseJoinColumns = @JoinColumn(name = "topic_id")
    )
    @Builder.Default
    private List<Topic> topics = new ArrayList<>();
    private String topic;
    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(name = "option_a", nullable = false) private String optionA;
    @Column(name = "option_b", nullable = false) private String optionB;
    @Column(name = "option_c", nullable = false) private String optionC;
    @Column(name = "option_d", nullable = false) private String optionD;

    @Column(name = "correct_option", nullable = false)
    private String correctOption;  // "A", "B", "C", or "D"

    @Column(name = "solution_text", columnDefinition = "TEXT")
    private String solutionText;

    @Column(name = "hint_text", columnDefinition = "TEXT")
    private String hintText;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() { this.createdAt = LocalDateTime.now(); }

    @Column(name = "image_url")
    private String imageUrl;
}
