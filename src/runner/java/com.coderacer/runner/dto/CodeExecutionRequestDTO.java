package com.coderacer.runner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeExecutionRequestDTO {
    private String code;
    private List<Integer> inputData;
}