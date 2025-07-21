package com.coderacer.service;

import com.coderacer.client.CodeExecutionClient;
import com.coderacer.dto.ExecutionResultDto;
import com.coderacer.dto.TestResultDto;
import com.coderacer.dto.TestSubmissionDto;
import com.coderacer.dto.TestCaseResultDto;
import com.coderacer.exception.CodingProblemNotFoundException;
import com.coderacer.exception.TestCaseIndexOutOfBoundsException;
import com.coderacer.model.CodingProblem;
import com.coderacer.model.TestCase;
import com.coderacer.repository.CodingProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestingService {

    private final CodingProblemRepository codingProblemRepository;
    private final CodeExecutionClient codeExecutionClient;

    /**
     * Tests user's code against all test cases of a coding problem
     */
    public TestResultDto testCodeAgainstProblem(UUID problemId, TestSubmissionDto submission) {
        CodingProblem problem = codingProblemRepository.findById(problemId)
                .orElseThrow(() -> new CodingProblemNotFoundException("Problem not found: " + problemId));

        List<TestCaseResultDto> testCaseResults = new ArrayList<>();
        boolean allTestsPassed = true;

        for (int i = 0; i < problem.getTestCases().size(); i++) {
            TestCase testCase = problem.getTestCases().get(i);
            TestCaseResultDto result = executeTestCase(submission.getCode(), testCase, i);
            testCaseResults.add(result);

            if (!result.isPassed()) {
                allTestsPassed = false;
            }
        }

        return TestResultDto.builder()
                .problemId(problemId)
                .allTestsPassed(allTestsPassed)
                .totalTestCases(problem.getTestCases().size())
                .passedTestCases((int) testCaseResults.stream().filter(TestCaseResultDto::isPassed).count())
                .testCaseResults(testCaseResults)
                .build();
    }

    /**
     * Tests user's code against a single test case
     */
    public TestResultDto testCodeAgainstSingleTestCase(UUID problemId, int testCaseIndex,
                                                       TestSubmissionDto submission) {
        CodingProblem problem = codingProblemRepository.findById(problemId)
                .orElseThrow(() -> new CodingProblemNotFoundException("Problem not found: " + problemId));

        if (testCaseIndex < 0 || testCaseIndex >= problem.getTestCases().size()) {
            throw new TestCaseIndexOutOfBoundsException(
                    "Test case index " + testCaseIndex + " is out of bounds");
        }

        TestCase testCase = problem.getTestCases().get(testCaseIndex);
        TestCaseResultDto result = executeTestCase(submission.getCode(), testCase, testCaseIndex);

        return TestResultDto.builder()
                .problemId(problemId)
                .allTestsPassed(result.isPassed())
                .totalTestCases(1)
                .passedTestCases(result.isPassed() ? 1 : 0)
                .testCaseResults(List.of(result))
                .build();
    }

    /**
     * Executes a single test case and compares output
     */
    private TestCaseResultDto executeTestCase(String code, TestCase testCase, int testCaseIndex) {
        try {
            // Convert inputs to a single string (assuming newline-separated)
            String inputString = String.join("\n", testCase.getInputs());

            // Call the code execution microservice
            ExecutionResultDto executionResult = codeExecutionClient.executeCode(code, inputString);

            if (!executionResult.isSuccess()) {
                return TestCaseResultDto.builder()
                        .testCaseIndex(testCaseIndex)
                        .passed(false)
                        .expectedOutput(testCase.getExpectedOutputs())
                        .actualOutput(List.of())
                        .error(executionResult.getError())
                        .executionTimeMs(executionResult.getExecutionTimeMs())
                        .build();
            }

            // Parse the output string back into lines
            List<String> actualOutput = parseOutputString(executionResult.getOutput());

            // Compare expected vs actual output
            boolean passed = compareOutputs(testCase.getExpectedOutputs(), actualOutput);

            return TestCaseResultDto.builder()
                    .testCaseIndex(testCaseIndex)
                    .passed(passed)
                    .expectedOutput(testCase.getExpectedOutputs())
                    .actualOutput(actualOutput)
                    .executionTimeMs(executionResult.getExecutionTimeMs())
                    .build();

        } catch (Exception e) {
            log.error("Error executing test case {}: {}", testCaseIndex, e.getMessage());
            return TestCaseResultDto.builder()
                    .testCaseIndex(testCaseIndex)
                    .passed(false)
                    .expectedOutput(testCase.getExpectedOutputs())
                    .actualOutput(List.of())
                    .error("Execution error: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Parses the output string from the execution service into lines
     */
    private List<String> parseOutputString(String output) {
        if (output == null || output.trim().isEmpty()) {
            return List.of();
        }

        return List.of(output.split("\n"));
    }

    /**
     * Compares expected vs actual output, handling whitespace and empty lines
     */
    private boolean compareOutputs(List<String> expected, List<String> actual) {
        if (expected.size() != actual.size()) {
            return false;
        }

        for (int i = 0; i < expected.size(); i++) {
            String expectedLine = expected.get(i).trim();
            String actualLine = actual.size() > i ? actual.get(i).trim() : "";

            if (!expectedLine.equals(actualLine)) {
                return false;
            }
        }

        return true;
    }
}