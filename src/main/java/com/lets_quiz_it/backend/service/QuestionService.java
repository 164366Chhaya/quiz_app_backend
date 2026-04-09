package com.lets_quiz_it.backend.service;

import com.lets_quiz_it.backend.dto.BulkImportResultDTO;
import com.lets_quiz_it.backend.dto.QuestionImportDTO;
import com.lets_quiz_it.backend.entity.Question;
import com.lets_quiz_it.backend.entity.Topic;
import com.lets_quiz_it.backend.repository.QuestionRepository;
import com.lets_quiz_it.backend.repository.TopicRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final TopicRepository topicRepository;
    private final QidGeneratorService qidGeneratorService;

    @Transactional
    public BulkImportResultDTO bulkImport(List<QuestionImportDTO> dtos) {
        int success = 0, failure = 0;
        List<String> errors = new ArrayList<>();
        List<Question> batchToSave = new ArrayList<>();

        // Pre-load all topics into a map to avoid N+1 DB calls
        Map<String, Topic> topicCache = topicRepository.findAll()
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                        t -> t.getName().toLowerCase(),
                        t -> t
                ));

        for (QuestionImportDTO dto : dtos) {
            try {
                // ── Resolve topics (supports both single and multi-topic) ──
                List<String> topicNames = new ArrayList<>();
                if (dto.getTopics() != null && !dto.getTopics().isEmpty()) {
                    topicNames = dto.getTopics();  // multi-topic
                } else if (dto.getTopic() != null && !dto.getTopic().isBlank()) {
                    topicNames = List.of(dto.getTopic());  // single topic
                }

                if (topicNames.isEmpty()) {
                    errors.add((dto.getQid() != null ? dto.getQid() : "UNKNOWN")
                            + ": No topic provided");
                    failure++;
                    continue;
                }

                // ── Resolve topic objects ──────────────────────────────────
                List<Topic> resolvedTopics = topicNames.stream()
                        .map(name -> topicCache.get(name.toLowerCase().trim()))
                        .filter(Objects::nonNull)
                        .toList();

                if (resolvedTopics.isEmpty()) {
                    errors.add((dto.getQid() != null ? dto.getQid() : "UNKNOWN")
                            + ": None of the topics found → " + topicNames);
                    failure++;
                    continue;
                }

                // ── Auto-generate QID if missing ───────────────────────────
                String qid = dto.getQid();
                if (qid == null || qid.isBlank()) {
                    List<String> resolvedTopicNames = topicNames;
                    qid = qidGeneratorService.generateQid(resolvedTopicNames);
                }

                // ── Check duplicate ────────────────────────────────────────
                if (questionRepository.existsByQid(qid)) {
                    errors.add(qid + ": already exists, skipped");
                    failure++;
                    continue;
                }

                // ── Build question ─────────────────────────────────────────
                Question q = Question.builder()
                        .qid(qid)
                        .topics(resolvedTopics)
                        .questionText(dto.getQuestion_text())
                        .optionA(dto.getOption_a())
                        .optionB(dto.getOption_b())
                        .optionC(dto.getOption_c())
                        .optionD(dto.getOption_d())
                        .correctOption(dto.getCorrect_option())
                        .solutionText(dto.getSolution_text())
                        .hintText(dto.getHint_text())
                        .imageUrl(dto.getImage_url())
                        .build();

                batchToSave.add(q);
                success++;

                // Save every 50 to avoid memory overload
                if (batchToSave.size() >= 50) {
                    questionRepository.saveAll(batchToSave);
                    batchToSave.clear();
                }

            } catch (Exception e) {
                errors.add((dto.getQid() != null ? dto.getQid() : "UNKNOWN")
                        + ": " + e.getMessage());
                failure++;
            }
        }

        // Save remaining batch
        if (!batchToSave.isEmpty()) {
            questionRepository.saveAll(batchToSave);
        }

        return new BulkImportResultDTO(success, failure, errors);
    }



    // ─── Single Question ──────────────────────────────────────────
    public Question getById(String question_id) {
        return questionRepository.findById(question_id)
                .orElseThrow(() -> new RuntimeException("Question not found: " + question_id));
    }

    public Question update(String question_id, QuestionImportDTO dto) {
        Question q = getById(question_id);
        q.setQuestionText(dto.getQuestion_text());
        q.setOptionA(dto.getOption_a());
        q.setOptionB(dto.getOption_b());
        q.setOptionC(dto.getOption_c());
        q.setOptionD(dto.getOption_d());
        q.setCorrectOption(dto.getCorrect_option());
        q.setSolutionText(dto.getSolution_text());
        q.setHintText(dto.getHint_text());
        return questionRepository.save(q);
    }

    public void delete(String question_id) {
        questionRepository.deleteById(question_id);
    }

    public List<Question> getByTopic(Long topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));
        return questionRepository.findByTopic(topic);
    }

    public List<Topic> resolveTopics(QuestionImportDTO dto, Map<String, Topic> cache) {
        List<String> topicNames = new ArrayList<>();
        if (dto.getTopics() != null && !dto.getTopics().isEmpty()) {
            topicNames = dto.getTopics();
        } else if (dto.getTopic() != null) {
            topicNames = List.of(dto.getTopic());
        }
        return topicNames.stream()
                .map(name -> cache.get(name.toLowerCase().trim()))
                .filter(Objects::nonNull)
                .toList();
    }
}