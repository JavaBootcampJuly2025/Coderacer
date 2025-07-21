package com.coderacer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestResultDto {
    private UUID problemId;
    private int totalTests;
    private int passedTests;
    private boolean allPassed;
}