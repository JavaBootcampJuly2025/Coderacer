package com.coderacer.service;

import com.coderacer.client.CodeExecutionClient;
import com.coderacer.dto.ExecutionResultDTO;
import com.coderacer.dto.TestResultDTO;
import com.coderacer.exception.CodingProblemNotFoundException;
import com.coderacer.model.CodingProblem;
import com.coderacer.repository.CodingProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestingService {

    private final CodingProblemRepository codingProblemRepository;
    private final CodeExecutionClient codeExecutionClient;

    public TestResultDTO testCode(UUID problemId, String code) {
        CodingProblem problem = codingProblemRepository.findById(problemId)
                .orElseThrow(() -> new CodingProblemNotFoundException("Problem not found: " + problemId));

        List<Integer> inputs = problem.getInputs();
        List<Integer> expectedOutputs = problem.getOutputs();

        ExecutionResultDTO result = codeExecutionClient.executeCode(code, inputs);

        // Handle non-success cases (compilation error, runtime error, timeout)
        if (result.getResult() != ExecutionResultDTO.Result.SUCCESS) {
            return TestResultDTO.builder()
                    .problemId(problemId)
                    .totalTests(expectedOutputs.size())
                    .passedTests(0)
                    .allPassed(false)
                    .executionStatus(result.getResult())
                    .actualOutput(result.getOutputLines()) // May contain error messages
                    .expectedOutput(expectedOutputs)
                    .errorMessage(getErrorMessage(result))
                    .build();
        }

        // Handle successful execution
        List<String> actualOutputs = result.getOutputLines();
        int total = expectedOutputs.size();
        int passed = countMatchingOutputs(expectedOutputs, actualOutputs);

        return TestResultDTO.builder()
                .problemId(problemId)
                .totalTests(total)
                .passedTests(passed)
                .allPassed(passed == total)
                .executionStatus(result.getResult())
                .actualOutput(actualOutputs)
                .expectedOutput(expectedOutputs)
                .errorMessage(null) // No error for successful execution
                .build();
    }

    private int countMatchingOutputs(List<Integer> expected, List<String> actual) {
        int passed = 0;
        int minSize = Math.min(expected.size(), actual.size());

        for (int i = 0; i < minSize; i++) {
            try {
                int actualInt = Integer.parseInt(actual.get(i).trim());
                if (actualInt == expected.get(i)) {
                    passed++;
                }
            } catch (NumberFormatException e) {
                // Mismatch if not an integer
            }
        }

        return passed;
    }

    private String getErrorMessage(ExecutionResultDTO result) {
        // Extract meaningful error message from output lines if available
        if (result.getOutputLines() != null && !result.getOutputLines().isEmpty()) {
            return String.join("\n", result.getOutputLines());
        }

        // Fallback to generic message based on result type
        return switch (result.getResult()) {
            case COMPILATION_ERROR -> "Code compilation failed";
            case RUNTIME_ERROR -> "Runtime error occurred during execution";
            case TIMEOUT -> "Code execution timed out";
            case OUTPUT_MISMATCH -> "Output does not match expected results";
            default -> "Unknown error occurred";
        };
    }
}