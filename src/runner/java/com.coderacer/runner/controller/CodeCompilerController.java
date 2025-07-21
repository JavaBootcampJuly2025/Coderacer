package com.coderacer.runner.controller;

import com.coderacer.runner.service.CodeExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for handling code compilation and execution requests.
 */
@RestController
@RequestMapping("/api/v1/code")
public class CodeCompilerController {

    private final CodeExecutionService codeExecutionService;

    @Autowired
    public CodeCompilerController(CodeExecutionService codeExecutionService) {
        this.codeExecutionService = codeExecutionService;
    }

    /**
     * Endpoint to compile and run Java code provided in the request body.
     *
     * @param code The Java code string.
     * @return A ResponseEntity containing the combined output (stdout and stderr)
     * from the compilation and execution process.
     */
    @PostMapping("/execute")
    public ResponseEntity<String> executeCode(@RequestBody String code) {
        if (code == null || code.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Java code cannot be empty.");
        }
        String result = codeExecutionService.compileAndRun(code);
        return ResponseEntity.ok(result);
    }
}