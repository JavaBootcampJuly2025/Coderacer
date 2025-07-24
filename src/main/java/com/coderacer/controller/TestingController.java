package com.coderacer.controller;

import com.coderacer.dto.TestResultDTO;
import com.coderacer.service.TestingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestingController {

    private final TestingService testingService;

    @PostMapping("/problem/{problemId}")
    @PreAuthorize("permitAll()")
    public TestResultDTO testCode(@PathVariable UUID problemId, @RequestBody String code) {
        return testingService.testCode(problemId, code);
    }
}