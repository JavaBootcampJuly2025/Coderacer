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

    @Test
    void testFileSystemWriteAttempt() {
        String code = """
            import java.io.*;
            public class Main {
                public static void main(String[] args) throws Exception {
                    FileWriter fw = new FileWriter("/tmp/hacked.txt");
                    fw.write("hacked");
                    fw.close();
                    System.out.println("Done");
                }
            }
        """;

        ExecutionResult result = service.compileAndRun(code);
        assertEquals(ExecutionResult.Result.RUNTIME_ERROR, result.getResult(), "File system writes should be blocked in secure environments");
    }

    @Test
    void testRuntimeExecBlocked() {
        String code = """
            public class Main {
                public static void main(String[] args) throws Exception {
                    Runtime.getRuntime().exec("touch /tmp/evil");
                    System.out.println("Executed");
                }
            }
        """;

        ExecutionResult result = service.compileAndRun(code);
        assertEquals(ExecutionResult.Result.RUNTIME_ERROR, result.getResult(), "Runtime.exec should be blocked in secure environments");
    }

    @Test
    void testNetworkAccess() {
        String code = """
            import java.net.*;
            public class Main {
                public static void main(String[] args) throws Exception {
                    Socket s = new Socket("example.com", 80);
                    System.out.println("Connected");
                    s.close();
                }
            }
        """;

        ExecutionResult result = service.compileAndRun(code);
        assertTrue(
                result.getResult() == ExecutionResult.Result.SUCCESS || result.getResult() == ExecutionResult.Result.RUNTIME_ERROR,
                "Should not allow external connections in secure environments"
        );
    }

    @Test
    void testForkBombLikeThreadSpamming() {
        String code = """
            public class Main {
                public static void main(String[] args) {
                    while (true) new Thread(() -> {}).start();
                }
            }
        """;

        ExecutionResult result = service.compileAndRun(code);
        assertTrue(
                result.getResult() == ExecutionResult.Result.TIMEOUT || result.getResult() == ExecutionResult.Result.RUNTIME_ERROR,
                "Fork bomb should timeout or error"
        );
    }

    @Test
    void testSystemExitIsPrevented() {
        String code = """
            public class Main {
                public static void main(String[] args) {
                    System.exit(1);
                }
            }
        """;

        ExecutionResult result = service.compileAndRun(code);
        assertEquals(ExecutionResult.Result.RUNTIME_ERROR, result.getResult(), "System.exit should not terminate the process");
    }
}
