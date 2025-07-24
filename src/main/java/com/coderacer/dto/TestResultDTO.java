package com.coderacer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestResultDTO {
    private UUID problemId;
    private int totalTests;
    private int passedTests;
    private boolean allPassed;

    private ExecutionResultDTO.Result executionStatus;
    private List<String> actualOutput;
    private List<Integer> expectedOutput;
    private String errorMessage;
}