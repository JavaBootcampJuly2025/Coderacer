package com.coderacer.service;

import com.coderacer.client.CodeExecutionClient;
import com.coderacer.dto.ExecutionResultDTO;
import com.coderacer.dto.TestResultDTO;
import com.coderacer.exception.CodingProblemNotFoundException;
import com.coderacer.model.CodingProblem;
import com.coderacer.model.TestCase;
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

        List<TestCase> testCases = problem.getTestCases();
        int totalTests = testCases.size();
        int passedTests = 0;

        for (TestCase testCase : testCases) {
            if (runSingleTest(code, testCase)) {
                passedTests++;
            }
        }

        return TestResultDTO.builder()
                .problemId(problemId)
                .totalTests(totalTests)
                .passedTests(passedTests)
                .allPassed(passedTests == totalTests)
                .build();
    }

    private boolean runSingleTest(String code, TestCase testCase) {
        try {

            List<Integer> inputToPass = testCase.getInputs();
            ExecutionResultDTO result = codeExecutionClient.executeCode(code, inputToPass);

            // Check if execution was successful
            if (result.getResult() != ExecutionResultDTO.Result.SUCCESS) {
                return false;
            }

            // Compare actual output with expected
            List<String> actualOutput = result.getOutputLines();
            List<String> expectedOutput = testCase.getExpectedOutputs();

            return compareOutputs(expectedOutput, actualOutput);

        } catch (Exception e) {
            log.error("Error executing test case: {}", e.getMessage());
            return false;
        }
    }

    private boolean compareOutputs(List<String> expected, List<String> actual) {
        if (expected.size() != actual.size()) {
            return false;
        }

        for (int i = 0; i < expected.size(); i++) {
            String expectedLine = expected.get(i).trim();
            String actualLine = actual.get(i).trim();
            if (!expectedLine.equals(actualLine)) {
                return false;
            }
        }

        return true;
    }
}