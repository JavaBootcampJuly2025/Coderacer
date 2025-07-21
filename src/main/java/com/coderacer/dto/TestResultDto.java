package com.coderacer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestResultDto {
    private UUID problemId;
    private boolean allTestsPassed;
    private int totalTestCases;
    private int passedTestCases;
    private List<TestCaseResultDto> testCaseResults;
}