package com.lets_quiz_it.backend.service;

import com.lets_quiz_it.backend.dto.*;
import com.lets_quiz_it.backend.entity.*;
import com.lets_quiz_it.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;
    private final QuestionService questionService;
    private final QuizSessionRepository quizSessionRepository;
    private final SessionQuestionRepository sessionQuestionRepository;
    private final UserTopicProgressRepository progressRepository;
    private final PendingReviewRepository pendingReviewRepository;

    // ─── Start Quiz ───────────────────────────────────────────────
    @Transactional
    public QuizStartResponseDTO startQuiz(String email, QuizConfigDTO config) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ── Resolve all selected topics ───────────────────────────
        List<Topic> selectedTopics = config.getTopicIds().stream()
                .map(id -> topicRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Topic not found: " + id)))
                .toList();

        // ── Fetch ALL available questions for selected topics ─────
        List<Question> allAvailable = questionRepository.findAllByTopicsIn(selectedTopics);
        long totalAvailable = allAvailable.size();

        if (totalAvailable == 0) {
            throw new RuntimeException(
                    "No questions found for selected topics. Import questions first."
            );
        }

        List<Question> allQuestions = new ArrayList<>();

        // ── 1. Pending review questions (wrong/skipped before) ────
        List<String> pendingQids = pendingReviewRepository
                .findByUserAndQuestion_TopicsIn(user, selectedTopics)
                .stream()
                .map(pr -> pr.getQuestion().getQid())
                .distinct()
                .toList();

        int maxPending = config.getNumberOfQuestions() / 2;
        List<String> pendingToAdd = pendingQids.stream()
                .limit(maxPending)
                .toList();

        // Add pending questions (in same order as allAvailable for consistency)
        Set<String> pendingSet = new HashSet<>(pendingToAdd);
        List<Question> pendingQuestions = allAvailable.stream()
                .filter(q -> pendingSet.contains(q.getQid()))
                .limit(maxPending)
                .toList();

        allQuestions.addAll(pendingQuestions);

        Set<String> addedQids = pendingQuestions.stream()
                .map(Question::getQid)
                .collect(Collectors.toSet());

        int remainingNeeded = config.getNumberOfQuestions() - pendingQuestions.size();

        // ── 2. Fill remaining from batch pointer ──────────────────
        if (remainingNeeded > 0) {
            Topic primaryTopic = selectedTopics.get(0);
            UserTopicProgress progress = progressRepository
                    .findByUserAndTopic(user, primaryTopic)
                    .orElseGet(() -> progressRepository.save(
                            UserTopicProgress.builder()
                                    .user(user)
                                    .topic(primaryTopic)
                                    .currentPointer(0)
                                    .cycleCount(0)
                                    .build()
                    ));

            int pointer = progress.getCurrentPointer();

            // Reset pointer if it exceeds total
            if (pointer >= totalAvailable) {
                pointer = 0;
                progress.setCycleCount(progress.getCycleCount() + 1);
            }

            // Walk through allAvailable starting at pointer
            // Skip questions already added (pending review)
            // Wrap around once if needed
            List<Question> batch = new ArrayList<>();
            int currentIndex = pointer;
            int wraps = 0;

            while (batch.size() < remainingNeeded && wraps < 2) {
                Question q = allAvailable.get(currentIndex);
                if (!addedQids.contains(q.getQid())) {
                    batch.add(q);
                    addedQids.add(q.getQid());
                }
                currentIndex++;
                if (currentIndex >= totalAvailable) {
                    currentIndex = 0;
                    wraps++;
                    progress.setCycleCount(progress.getCycleCount() + 1);
                }
            }

            // Save updated pointer
            progress.setCurrentPointer(currentIndex);
            progressRepository.save(progress);

            allQuestions.addAll(batch);
        }

        if (allQuestions.isEmpty()) {
            throw new RuntimeException(
                    "No questions available. Please import questions for these topics."
            );
        }

        // ── 3. Shuffle ────────────────────────────────────────────
        Collections.shuffle(allQuestions);

        // ── 4. Create session ─────────────────────────────────────
        QuizSession session = QuizSession.builder()
                .user(user)
                .topicIds(config.getTopicIds().toString())
                .totalQuestions(allQuestions.size())
                .negativeMarksApplied(config.isNegativeMarking())
                .startedAt(LocalDateTime.now())
                .build();
        quizSessionRepository.save(session);

        // ── 5. Build response (correct answers NOT exposed) ───────
        List<QuestionDTO> questionDTOs = allQuestions.stream().map(q ->
                QuestionDTO.builder()
                        .qid(q.getQid())
                        .topicName(q.getTopics().stream()
                                .map(Topic::getName)
                                .collect(Collectors.joining(", ")))
                        .questionText(q.getQuestionText())
                        .optionA(q.getOptionA())
                        .optionB(q.getOptionB())
                        .optionC(q.getOptionC())
                        .optionD(q.getOptionD())
                        .hintText(q.getHintText())
                        .build()
        ).toList();

        return new QuizStartResponseDTO(
                session.getId(),
                questionDTOs,
                config.getTimerPerQuestion(),
                config.isNegativeMarking(),
                config.getNegativeMarkRatio()
        );
    }

    // ─── Submit Quiz ──────────────────────────────────────────────
    @Transactional
    public QuizResultDTO submitQuiz(String email, QuizSubmitDTO submitDTO) {
        QuizSession session = quizSessionRepository
                .findById(submitDTO.getSessionId())
                .orElseThrow(() -> new RuntimeException("Session not found"));

        int correct = 0, incorrect = 0, skipped = 0;
        double negativeDeducted = 0;
        List<QuizResultDTO.QuestionReviewDTO> reviews = new ArrayList<>();

        double negRatio = session.isNegativeMarksApplied()
                ? submitDTO.getNegativeMarkRatio()
                : 0;

        for (QuizSubmitDTO.QuestionAnswerDTO ans : submitDTO.getAnswers()) {
            Question q = questionRepository.findById(ans.getQid())
                    .orElseThrow(() -> new RuntimeException("Question not found: " + ans.getQid()));

            boolean isSkipped = ans.getUserAnswer() == null
                    || ans.getUserAnswer().isBlank();
            boolean isCorrect = !isSkipped &&
                    ans.getUserAnswer().equalsIgnoreCase(q.getCorrectOption());

            if (isSkipped) skipped++;
            else if (isCorrect) correct++;
            else {
                incorrect++;
                if (session.isNegativeMarksApplied())
                    negativeDeducted += negRatio;
            }

            // ── Update pending review — SINGLE PLACE ─────────────
            if (isCorrect) {
                // Answered correctly — remove from pending if present
                pendingReviewRepository.deleteByUserAndQuestion(session.getUser(), q);
            } else {
                // Wrong or skipped — add to pending only if not already there
                if (!pendingReviewRepository.existsByUserAndQuestion(session.getUser(), q)) {
                    pendingReviewRepository.save(
                            PendingReview.builder()
                                    .user(session.getUser())
                                    .question(q)
                                    .reason(isSkipped
                                            ? PendingReview.Reason.SKIPPED
                                            : PendingReview.Reason.INCORRECT)
                                    .build()
                    );
                }
            }
            // ─────────────────────────────────────────────────────

            sessionQuestionRepository.save(
                    SessionQuestion.builder()
                            .session(session)
                            .question(q)
                            .userAnswer(ans.getUserAnswer())
                            .correct(isCorrect)
                            .skipped(isSkipped)
                            .timeTakenSeconds(ans.getTimeTakenSeconds())
                            .build()
            );

            reviews.add(QuizResultDTO.QuestionReviewDTO.builder()
                    .qid(q.getQid())
                    .questionText(q.getQuestionText())
                    .optionA(q.getOptionA())
                    .optionB(q.getOptionB())
                    .optionC(q.getOptionC())
                    .optionD(q.getOptionD())
                    .correctOption(q.getCorrectOption())
                    .userAnswer(ans.getUserAnswer())
                    .solutionText(q.getSolutionText())
                    .hintText(q.getHintText())
                    .correct(isCorrect)
                    .skipped(isSkipped)
                    .build()
            );
        }

        double score = correct - negativeDeducted;
        double accuracy = session.getTotalQuestions() == 0 ? 0 :
                (correct * 100.0) / session.getTotalQuestions();

        session.setCorrect(correct);
        session.setIncorrect(incorrect);
        session.setSkipped(skipped);
        session.setScore(score);
        session.setEndedAt(LocalDateTime.now());
        quizSessionRepository.save(session);

        long timeTaken = ChronoUnit.SECONDS.between(
                session.getStartedAt(), session.getEndedAt()
        );

        return QuizResultDTO.builder()
                .sessionId(session.getId())
                .totalQuestions(session.getTotalQuestions())
                .correct(correct)
                .incorrect(incorrect)
                .skipped(skipped)
                .score(score)
                .accuracy(accuracy)
                .timeTakenSeconds(timeTaken)
                .negativeMarking(session.isNegativeMarksApplied())
                .negativeMarksDeducted(negativeDeducted)
                .recommendation(getRecommendation(accuracy))
                .questionReviews(reviews)
                .build();
    }

    // ─── Recommendation ───────────────────────────────────────────
    private String getRecommendation(double accuracy) {
        if (accuracy < 40) return "Needs serious revision. Focus on basics of these topics.";
        if (accuracy < 60) return "Fair performance. Revisit incorrect questions and retry.";
        if (accuracy < 80) return "Good. Push for consistency. Attempt more sessions.";
        return "Excellent! Move to harder topics or increase question count.";
    }

    // ─── Get Session Result ───────────────────────────────────────
    public QuizResultDTO getSessionResult(Long sessionId) {
        QuizSession session = quizSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        List<SessionQuestion> sqs = sessionQuestionRepository.findBySession(session);
        List<QuizResultDTO.QuestionReviewDTO> reviews = sqs.stream().map(sq ->
                QuizResultDTO.QuestionReviewDTO.builder()
                        .qid(sq.getQuestion().getQid())
                        .questionText(sq.getQuestion().getQuestionText())
                        .optionA(sq.getQuestion().getOptionA())
                        .optionB(sq.getQuestion().getOptionB())
                        .optionC(sq.getQuestion().getOptionC())
                        .optionD(sq.getQuestion().getOptionD())
                        .correctOption(sq.getQuestion().getCorrectOption())
                        .userAnswer(sq.getUserAnswer())
                        .solutionText(sq.getQuestion().getSolutionText())
                        .hintText(sq.getQuestion().getHintText())
                        .correct(sq.isCorrect())
                        .skipped(sq.isSkipped())
                        .build()
        ).toList();

        double accuracy = session.getTotalQuestions() == 0 ? 0 :
                (session.getCorrect() * 100.0) / session.getTotalQuestions();

        return QuizResultDTO.builder()
                .sessionId(session.getId())
                .totalQuestions(session.getTotalQuestions())
                .correct(session.getCorrect())
                .incorrect(session.getIncorrect())
                .skipped(session.getSkipped())
                .score(session.getScore())
                .accuracy(accuracy)
                .negativeMarking(session.isNegativeMarksApplied())
                .recommendation(getRecommendation(accuracy))
                .questionReviews(reviews)
                .build();
    }
}