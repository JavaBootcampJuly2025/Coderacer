package com.coderacer.controller;

import com.coderacer.dto.TestResultDto;
import com.coderacer.service.TestingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestingController {

    private final TestingService testingService;

    @PostMapping("/problem/{problemId}")
    public TestResultDto testCode(@PathVariable UUID problemId, @RequestBody String code) {
        return testingService.testCode(problemId, code);
    }

    @PostMapping("/problem/{problemId}/testcase/{testCaseIndex}")
    public TestResultDto testSingleTestCase(@PathVariable UUID problemId, @PathVariable int testCaseIndex, @RequestBody String code) {
        return testingService.testSingleTestCase(problemId, testCaseIndex, code);
    }
}