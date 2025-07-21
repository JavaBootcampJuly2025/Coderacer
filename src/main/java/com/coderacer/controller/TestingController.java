package com.coderacer.controller;

import com.coderacer.dto.TestResultDto;
import com.coderacer.dto.TestSubmissionDto;
import com.coderacer.service.TestingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestingController {

    private final TestingService testingService;

    /**
     * Test user's code against all test cases for a specific coding problem
     */
    @PostMapping("/problem/{problemId}")
    public ResponseEntity<TestResultDto> testCode(
            @PathVariable UUID problemId,
            @Valid @RequestBody TestSubmissionDto submission) {

        TestResultDto result = testingService.testCodeAgainstProblem(problemId, submission);
        return ResponseEntity.ok(result);
    }

    /**
     * Test user's code against a specific test case (useful for debugging)
     */
    @PostMapping("/problem/{problemId}/testcase/{testCaseIndex}")
    public ResponseEntity<TestResultDto> testSingleTestCase(
            @PathVariable UUID problemId,
            @PathVariable int testCaseIndex,
            @Valid @RequestBody TestSubmissionDto submission) {

        TestResultDto result = testingService.testCodeAgainstSingleTestCase(
                problemId, testCaseIndex, submission);
        return ResponseEntity.ok(result);
    }
}