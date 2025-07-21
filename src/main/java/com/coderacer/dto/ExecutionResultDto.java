package com.coderacer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionResultDto {
    private boolean success;
    private String output;
    private String error;
    private Long executionTimeMs;
}