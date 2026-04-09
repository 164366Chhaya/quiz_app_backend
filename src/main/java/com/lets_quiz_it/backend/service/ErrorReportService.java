package com.lets_quiz_it.backend.service;

import com.lets_quiz_it.backend.dto.ErrorReportDTO;
import com.lets_quiz_it.backend.entity.*;
import com.lets_quiz_it.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ErrorReportService {

    private final ErrorReportRepository errorReportRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;

    // User submits an error report
    public String reportError(String email, ErrorReportDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Question question = questionRepository.findById(dto.getQid())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        errorReportRepository.save(
                ErrorReport.builder()
                        .question(question)
                        .reportedBy(user)
                        .comment(dto.getComment())
                        .status(ErrorReport.Status.OPEN)
                        .build()
        );
        return "Error reported successfully";
    }

    // Admin — get all open reports
    public List<ErrorReport> getOpenReports() {
        return errorReportRepository.findByStatus(ErrorReport.Status.OPEN);
    }

    // Admin — mark as fixed
    public String markFixed(Long reportId) {
        ErrorReport report = errorReportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        report.setStatus(ErrorReport.Status.FIXED);
        errorReportRepository.save(report);
        return "Marked as fixed";
    }
}