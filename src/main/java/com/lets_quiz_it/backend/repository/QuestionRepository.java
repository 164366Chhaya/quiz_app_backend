package com.lets_quiz_it.backend.repository;

import com.lets_quiz_it.backend.entity.Question;
import com.lets_quiz_it.backend.entity.Topic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, String> {


    @Query("SELECT DISTINCT q FROM Question q JOIN q.topics t WHERE t IN :topics ORDER BY q.createdAt ASC")
    List<Question> findAllByTopicsIn(@Param("topics") List<Topic> topics);

    @Query("SELECT COUNT(DISTINCT q) FROM Question q JOIN q.topics t WHERE t IN :topics")
    long countByTopicsIn(@Param("topics") List<Topic> topics);

    boolean existsByQid(String qid);
    long countByQidStartingWith(String prefix);

    long countByTopic(Topic topic);

    List<Question> findByTopic(Topic topic);
}