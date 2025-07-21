package com.coderacer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestCaseResultDto {
    private int testCaseIndex;
    private boolean passed;
    private List<String> expectedOutput;
    private List<String> actualOutput;
    private String error;
    private Long executionTimeMs;
}