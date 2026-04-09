package com.lets_quiz_it.backend.service;

import com.lets_quiz_it.backend.dto.BookmarkDTO;
import com.lets_quiz_it.backend.entity.*;
import com.lets_quiz_it.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;

    public List<BookmarkDTO> getBookmarks(String email) {
        User user = getUser(email);
        return bookmarkRepository.findByUser(user).stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public String toggleBookmark(String email, String question_id) {
        User user = getUser(email);
        Question question = questionRepository.findById(question_id)
                .orElseThrow(() -> new RuntimeException("Question not found: " + question_id));

        if (bookmarkRepository.existsByUserAndQuestion(user, question)) {
            bookmarkRepository.deleteByUserAndQuestion(user, question);
            return "Bookmark removed";
        } else {
            bookmarkRepository.save(
                    Bookmark.builder().user(user).question(question).build()
            );
            return "Bookmark added";
        }
    }

    private BookmarkDTO toDTO(Bookmark b) {
        Question q = b.getQuestion();
        return BookmarkDTO.builder()
                .bookmarkId(b.getId())
                .qid(q.getQid())
                .questionText(q.getQuestionText())
                .optionA(q.getOptionA())
                .optionB(q.getOptionB())
                .optionC(q.getOptionC())
                .optionD(q.getOptionD())
                .correctOption(q.getCorrectOption())
                .solutionText(q.getSolutionText())
                .hintText(q.getHintText())
                .topicName(q.getTopics().isEmpty() ? "" : q.getTopics().stream().map(Topic::getName).collect(java.util.stream.Collectors.joining(", ")))
                .bookmarkedAt(b.getBookmarkedAt())
                .build();
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}