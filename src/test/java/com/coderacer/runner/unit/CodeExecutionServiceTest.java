package com.coderacer.runner.unit;

import com.coderacer.runner.model.ExecutionResult;
import com.coderacer.runner.service.CodeExecutionService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Path;
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
        ReflectionTestUtils.setField(service, "useDocker", false); // disable docker for unit test
    }

    @Test
    void testSuccessfulExecution() {
        String code = """
            import java.util.Scanner;
            public class Main {
                public static void main(String[] args) {
                    Scanner sc = new Scanner(System.in);
                    sc.nextLine(); // consume the first line
                    int n = sc.nextInt();
                    System.out.println(n*2);
                }
            }
        """;
        List<Integer> input = new ArrayList<>();
        input.add(4);

        ExecutionResult result = service.compileAndRun(code, input);
        assertEquals(ExecutionResult.Result.SUCCESS, result.getResult());
        assertEquals(List.of("8"), result.getOutputLines());
    }

    @Test
    void testCompilationError() {
        String code = """
            public class Main {
                public static void main(String[] args) {
                    System.out.println("Missing semicolon")
                }
            }
        """;

        ExecutionResult result = service.compileAndRun(code);
        assertEquals(ExecutionResult.Result.COMPILATION_ERROR, result.getResult());
        assertTrue(result.getOutputLines().stream().anyMatch(line -> line.contains("';'")));
    }

    @Test
    void testRuntimeError() {
        String code = """
            public class Main {
                public static void main(String[] args) {
                    throw new RuntimeException("Boom!");
                }
            }
        """;

        ExecutionResult result = service.compileAndRun(code);
        assertEquals(ExecutionResult.Result.RUNTIME_ERROR, result.getResult());
        assertTrue(result.getOutputLines().stream().anyMatch(line -> line.contains("Exception")));
    }

    @Test
    void testInfiniteLoopTimeout() {
        String code = """
            public class Main {
                public static void main(String[] args) {
                    while (true) {}
                }
            }
        """;

        ExecutionResult result = service.compileAndRun(code);
        assertEquals(ExecutionResult.Result.TIMEOUT, result.getResult());
    }

    @Test
    void testNoInputRequired() {
        String code = """
            public class Main {
                public static void main(String[] args) {
                    System.out.println("Hello");
                }
            }
        """;

        ExecutionResult result = service.compileAndRun(code);
        assertEquals(ExecutionResult.Result.SUCCESS, result.getResult());
        assertEquals(List.of("Hello"), result.getOutputLines());
    }

    @Test
    void testMultipleInputs() {
        String code = """
            import java.util.Scanner;
            public class Main {
                public static void main(String[] args) {
                    Scanner scanner = new Scanner(System.in);
                    int n = scanner.nextInt();
                    for (int i = 0; i < n; i++) {
                        int val = scanner.nextInt();
                        System.out.println(val + 1);
                    }
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
        ReflectionTestUtils.setField(service, "useDocker", false);
        String code = """
            public class Main {
                public static void main(String[] args) {
                    System.out.println("Fallback test");
                }
            }
        """;
        ExecutionResult result = service.compileAndRun(code);
        assertEquals(ExecutionResult.Result.SUCCESS, result.getResult());
        assertEquals(List.of("Fallback test"), result.getOutputLines());
    }

    @Test
    void testLegacyMethodWithoutInput() {
        String code = """
            public class Main {
                public static void main(String[] args) {
                    System.out.println("Legacy");
                }
            }
        """;

        ExecutionResult result = service.compileAndRun(code);
        assertEquals(ExecutionResult.Result.SUCCESS, result.getResult());
        assertEquals(List.of("Legacy"), result.getOutputLines());
    }
}
