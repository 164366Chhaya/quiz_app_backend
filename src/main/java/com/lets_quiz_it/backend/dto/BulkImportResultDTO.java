package com.lets_quiz_it.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BulkImportResultDTO {
    private int successCount;
    private int failureCount;
    private java.util.List<String> errors;
}