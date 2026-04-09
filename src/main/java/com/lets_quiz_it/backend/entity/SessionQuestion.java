package com.lets_quiz_it.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "session_questions")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SessionQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private QuizSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "user_answer")       private String userAnswer;
    @Column(name = "is_correct")        private boolean correct;
    @Column(name = "is_skipped")        private boolean skipped;
    @Column(name = "time_taken_seconds") private int timeTakenSeconds;
}
