package com.lets_quiz_it.backend.repository;

import com.lets_quiz_it.backend.entity.PendingReview;
import com.lets_quiz_it.backend.entity.Question;
import com.lets_quiz_it.backend.entity.Topic;
import com.lets_quiz_it.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PendingReviewRepository extends JpaRepository<PendingReview, Long> {

    @Query("SELECT pr FROM PendingReview pr WHERE pr.user = :user " +
            "AND EXISTS (SELECT 1 FROM pr.question.topics t WHERE t IN :topics)")
    List<PendingReview> findByUserAndQuestion_TopicsIn(
            @Param("user") User user,
            @Param("topics") List<Topic> topics);

    void deleteByUserAndQuestion(User user, Question question);
    boolean existsByUserAndQuestion(User user, Question question);
}