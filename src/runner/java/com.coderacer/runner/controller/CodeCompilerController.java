package com.coderacer.runner.controller;

import com.coderacer.runner.model.ExecutionResult;
import com.coderacer.runner.service.CodeExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for handling code compilation and execution requests.
 *
 * The /execute endpoint expects:
 * - code: Java code string that must contain a public class with a main method
 * - inputData: Optional array of integers that will be fed to Scanner input
 *
 * Input handling:
 * - First, the count of integers is automatically provided to Scanner
 * - Then, each integer from inputData array is provided line by line
 * - This allows code using Scanner.nextInt() to read: count, then values
 *
 * Example request:
 * {
 *   "code": "import java.util.*; public class MySolution { public static void main(String[] args) { Scanner sc = new Scanner(System.in); int n = sc.nextInt(); int[] arr = new int[n]; for(int i = 0; i < n; i++) { arr[i] = sc.nextInt(); } sc.close(); // process arr } }",
 *   "inputData": [1, 2, 3, 4]
 * }
 *
 * This will provide to Scanner: 4 (count), then 1, 2, 3, 4 (values)
 */
@RestController
@RequestMapping("/api/code")
public class CodeCompilerController {

    private final CodeExecutionService codeExecutionService;

    @Autowired
    public CodeCompilerController(CodeExecutionService codeExecutionService) {
        this.codeExecutionService = codeExecutionService;
    }

    /**
     * Endpoint to compile and run Java code with input data
     */
    @PostMapping("/execute")
    public ResponseEntity<ExecutionResult> executeCode(@RequestBody Map<String, Object> request) {
        try {
            String code = (String) request.get("code");
            List<Integer> inputData = (List<Integer>) request.get("inputData");

            if (code == null || code.trim().isEmpty()) {
                ExecutionResult errorResult = new ExecutionResult();
                errorResult.setResult(ExecutionResult.Result.COMPILATION_ERROR);
                errorResult.getOutputLines().add("Code cannot be empty");
                return ResponseEntity.badRequest().body(errorResult);
            }

            ExecutionResult result = codeExecutionService.compileAndRun(code, inputData);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            ExecutionResult errorResult = new ExecutionResult();
            errorResult.setResult(ExecutionResult.Result.RUNTIME_ERROR);
            errorResult.getOutputLines().add("Controller error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResult);
        }
    }

    /**
     * Legacy endpoint for backward compatibility - accepts just code as string
     */
    @PostMapping("/execute-simple")
    public ResponseEntity<ExecutionResult> executeCodeSimple(@RequestBody String code) {
        try {
            if (code == null || code.trim().isEmpty()) {
                ExecutionResult errorResult = new ExecutionResult();
                errorResult.setResult(ExecutionResult.Result.COMPILATION_ERROR);
                errorResult.getOutputLines().add("Code cannot be empty");
                return ResponseEntity.badRequest().body(errorResult);
            }

            ExecutionResult result = codeExecutionService.compileAndRun(code, null);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            ExecutionResult errorResult = new ExecutionResult();
            errorResult.setResult(ExecutionResult.Result.RUNTIME_ERROR);
            errorResult.getOutputLines().add("Controller error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResult);
        }
    }
}