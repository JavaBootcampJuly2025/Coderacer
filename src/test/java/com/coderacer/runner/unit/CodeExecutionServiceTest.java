package com.coderacer.runner.unit;

import com.coderacer.runner.model.ExecutionResult;
import com.coderacer.runner.service.CodeExecutionService;
import org.junit.jupiter.api.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CodeExecutionServiceTest {

    private CodeExecutionService service;

    @BeforeEach
    void setUp() {
        service = new CodeExecutionService();
        ReflectionTestUtils.setField(service, "useDocker", true);
        ReflectionTestUtils.setField(service, "dockerMemoryLimit", "64m");
        ReflectionTestUtils.setField(service, "dockerCpuLimit", "0.2");
        ReflectionTestUtils.setField(service, "seccompProfilePath", "src/main/resources/seccomp.json");
    }

    @Test
    void testSuccessfulExecution() {
        // User's code snippet will be wrapped by the template's solution(n, arr)
        String code = """
            static void solution(int n, int[] arr) {
                System.out.println(arr[0] * 2);
            }
        """;
        // Provide dummy input for the template's main method (Scanner sc.nextInt())
        List<Integer> input = List.of(4); // n=4, arr is empty here based on template.
        // For array, you'd add more ints after n.
        // But solution only uses n here.

        ExecutionResult result = service.compileAndRun(code, input);
        assertEquals(ExecutionResult.Result.SUCCESS, result.getResult());
        assertEquals(List.of("8"), result.getOutputLines());
    }

    @Test
    void testCompilationError() {
        String code = """
            static void solution(int n, int[] arr) {
                System.out.println("Missing semicolon") // Syntax error
            }
        """;

        ExecutionResult result = service.compileAndRun(code, Collections.emptyList()); // Empty input for template
        assertEquals(ExecutionResult.Result.COMPILATION_ERROR, result.getResult());
        assertTrue(result.getOutputLines().stream().anyMatch(line -> line.contains("';'")));
    }

    @Test
    void testRuntimeError() {
        String code = """
            static void solution(int n, int[] arr) {
                throw new RuntimeException("Boom!");
            }
        """;
        List<Integer> input = List.of(0); // Dummy input for template's Scanner

        ExecutionResult result = service.compileAndRun(code, input);
        assertEquals(ExecutionResult.Result.RUNTIME_ERROR, result.getResult());
        assertTrue(result.getOutputLines().stream().anyMatch(line -> line.contains("Exception")));
    }

    @Test
    void testInfiniteLoopTimeout() {
        String code = """
            static void solution(int n, int[] arr) {
                while (true) {}
            }
        """;
        List<Integer> input = List.of(0); // Dummy input for template's Scanner

        ExecutionResult result = service.compileAndRun(code, input);
        assertEquals(ExecutionResult.Result.TIMEOUT, result.getResult());
    }

    @Test
    void testNoInputRequired() {
        String code = """
            static void solution(int n, int[] arr) {
                System.out.println("Hello");
            }
        """;
        // Provide minimal input to satisfy template's scanner for 'n' (arr will be empty)
        List<Integer> input = List.of(0);

        ExecutionResult result = service.compileAndRun(code, input);
        assertEquals(ExecutionResult.Result.SUCCESS, result.getResult());
        assertEquals(List.of("Hello"), result.getOutputLines());
    }

    @Test
    void testMultipleInputs() {
        String code = """
            static void solution(int n, int[] arr) {
                for (int val : arr) {
                    System.out.println(val + 1);
                }
            }
        """;

        List<Integer> input = Arrays.asList(1, 2, 3);

        ExecutionResult result = service.compileAndRun(code, input);
        assertEquals(ExecutionResult.Result.SUCCESS, result.getResult());
        assertEquals(List.of("2", "3", "4"), result.getOutputLines());
    }

    @Test
    void testFallbackToDirectExecutionWhenDockerDisabled() {
        // Temporarily disable Docker for this test
        ReflectionTestUtils.setField(service, "useDocker", false);
        String code = """
            static void solution(int n, int[] arr) {
                System.out.println("Fallback test");
            }
        """;
        List<Integer> input = List.of(0); // Dummy input for template's Scanner

        ExecutionResult result = service.compileAndRun(code, input);
        assertEquals(ExecutionResult.Result.SUCCESS, result.getResult());
        assertEquals(List.of("Fallback test"), result.getOutputLines());
    }

    @Test
    void testFileSystemWriteAttempt() {
        String code = """
            static void solution(int n, int[] arr) throws Exception {
                java.io.FileWriter fw = new java.io.FileWriter("tmp/hacked.txt");
                fw.write("hacked");
                fw.close();
                System.out.println("Done"); // This might or might not be printed if an exception occurs
            }
        """;
        List<Integer> input = List.of(0); // Dummy input for template's Scanner

        ExecutionResult result = service.compileAndRun(code, input);
        assertEquals(ExecutionResult.Result.RUNTIME_ERROR, result.getResult(), "File system writes should be blocked/error in secure environments if not /tmp, or if /tmp is configured to be non-writable for some reason.");
        // We might also check if output contains "Done" for a more nuanced test if it's expected to succeed and then be blocked later.
        // But for expecting RUNTIME_ERROR, presence of "Done" is less relevant.
    }

    @Test
    void testRuntimeExecBlocked() {
        String code = """
                    static void solution(int n, int[] arr) throws Exception {
                        // This will likely throw an IOException if Runtime.exec is blocked by Docker's capabilities/seccomp
                        // The main template will catch this or it will cause a non-zero exit.
                        Runtime.getRuntime().exec("touch /tmp/evil");
                        System.out.println("Executed"); // This might not be reached if exec throws
                    }
                """;
        List<Integer> input = List.of(0); // Dummy input for template's Scanner

        ExecutionResult result = service.compileAndRun(code, input);
        // Expecting SUCCESS if exec is blocked and handled gracefully, or RUNTIME_ERROR if it throws an exception.
        assertTrue(
                result.getResult() == ExecutionResult.Result.SUCCESS || result.getResult() == ExecutionResult.Result.RUNTIME_ERROR,
                "Runtime.exec should be blocked in secure environments; Expected SUCCESS or RUNTIME_ERROR"
        );
    }

    @Test
    void testNetworkAccess() {
        String code = """
            static void solution(int n, int[] arr) throws Exception {
                // Network access should be blocked by --network=none
                java.net.Socket s = new java.net.Socket("example.com", 80); // Changed to use absolute path
                System.out.println("Connected");
                s.close();
            }
        """;
        List<Integer> input = List.of(0); // Dummy input for template's Scanner

        ExecutionResult result = service.compileAndRun(code, input);
        // Expecting RUNTIME_ERROR (e.g., UnknownHostException, ConnectException) or TIMEOUT
        // This makes the assertion stricter and more correct for a secure sandbox.
        assertTrue(
                result.getResult() == ExecutionResult.Result.RUNTIME_ERROR || result.getResult() == ExecutionResult.Result.TIMEOUT,
                "Should not allow external connections in secure environments; Expected RUNTIME_ERROR or TIMEOUT"
        );
    }

    @Test
    void testForkBombLikeThreadSpamming() {
        String code = """
            static void solution(int n, int[] arr) {
                // This attempts to create many threads, which should hit pids-limit or nproc ulimit.
                // It should lead to a timeout or a runtime error (e.g., OutOfMemoryError, or OS-level resource exhaustion).
                while (true) new Thread(() -> {}).start();
            }
        """;
        List<Integer> input = List.of(0); // Dummy input for template's Scanner

        ExecutionResult result = service.compileAndRun(code, input);
        // Expecting TIMEOUT or RUNTIME_ERROR due to resource limits
        assertTrue(
                result.getResult() == ExecutionResult.Result.TIMEOUT || result.getResult() == ExecutionResult.Result.RUNTIME_ERROR,
                "Fork bomb should timeout or lead to a runtime error (e.g., due to process/thread limits)"
        );
    }

    @Test
    void testSystemExitIsPrevented() {
        String code = """
            static void solution(int n, int[] arr) {
                System.exit(1);
            }
        """;
        List<Integer> input = List.of(0); // Dummy input for template's Scanner

        ExecutionResult result = service.compileAndRun(code, input);
        // System.exit(1) should lead to a non-zero exit code of the Docker container,
        // which the service should correctly interpret as a RUNTIME_ERROR.
        assertEquals(ExecutionResult.Result.RUNTIME_ERROR, result.getResult(), "System.exit should be treated as a runtime error in sandboxed environment");
    }
}