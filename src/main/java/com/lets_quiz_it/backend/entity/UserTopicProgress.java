package com.lets_quiz_it.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_topic_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTopicProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column(name = "current_pointer", nullable = false)
    private int currentPointer = 0;

    @Column(name = "cycle_count", nullable = false)
    private int cycleCount = 0;        // ← this was missing!
}