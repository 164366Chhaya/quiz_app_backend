package com.lets_quiz_it.backend.repository;

import com.lets_quiz_it.backend.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ErrorReportRepository extends JpaRepository<ErrorReport, Long> {
    List<ErrorReport> findByStatus(ErrorReport.Status status);
    List<ErrorReport> findByReportedBy(User user);
}