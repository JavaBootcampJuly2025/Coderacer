package com.coderacer.runner.service;

import com.coderacer.runner.model.ExecutionResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Refactored service for compiling and executing Java code with optional Docker isolation.
 * Now automatically wraps standalone solution snippets in the template.
 *
 * Example working request:
 * {
 *   "code": "static void solution(int n, int[] arr) { System.out.println(\"hello world\"); }",
 *   "inputData": [1, 23, 2]
 * }
 *
 * OR
 *
 * {
 *   "code": "static void solution(int n, int[] arr) { System.out.println(n); }",
 *   "inputData": [1, 23, 2]
 * }
 */
@Service
public class CodeExecutionService {
    private static final long EXECUTION_TIMEOUT_SECONDS = 10;
    private static final String JAVA_TEMPLATE =
            """
                    import java.util.*;
                    public class %s {
                        public static void main(String[] args) {
                            Scanner sc = new Scanner(System.in);
                            int n = sc.nextInt();
                            int[] arr = new int[n];
                            for (int i = 0; i < n; i++) arr[i] = sc.nextInt();
                            sc.close();
                            solution(n, arr);
                        }
                        %s
                    }""";

    @Value("${code.execution.use-docker:true}")
    private boolean useDocker;
    @Value("${code.execution.docker.memory:64m}")
    private String dockerMemoryLimit= "64m";
    @Value("${code.execution.docker.cpu:0.2}")
    private String dockerCpuLimit = "0.2";
    @Value("${code.execution.docker.seccomp-profile:}")
    private String seccompProfilePath = "src/runner/java/com.coderacer/runner/security/seccomp.json";

    /**
     * Default entry: always wrap snippet in template so standalone methods compile.
     */
    public ExecutionResult compileAndRun(String code, List<Integer> inputData) {
        return executePipeline(code, inputData);
    }

    /**
     * Internal unified pipeline for setup, compile, and run.
     */
    private ExecutionResult executePipeline(String code, List<Integer> inputData) {
        String uniqueId = UUID.randomUUID().toString().replace("-", "");
        String className = "GeneratedClass_" + uniqueId;
        String fullCode = String.format(JAVA_TEMPLATE, className, code);

        boolean withDocker = useDocker && isDockerAvailable();
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "java_code_exec_" + uniqueId);
        ExecutionResult result = new ExecutionResult();

        try {
            prepareEnvironment(tempDir, className, fullCode, inputData);
            if (!compile(tempDir, className, withDocker, result)) return result;
            run(tempDir, className, inputData, withDocker, result);
        } catch (IOException | InterruptedException e) {
            result.setResult(ExecutionResult.Result.RUNTIME_ERROR);
            result.getOutputLines().add((withDocker ? "Docker" : "Direct") + " execution error: " + e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            cleanup(tempDir);
        }
        return result;
    }

    private void prepareEnvironment(Path tempDir, String className, String code, List<Integer> inputData) throws IOException {
        Files.createDirectories(tempDir);
        Files.write(tempDir.resolve(className + ".java"), code.getBytes());
        if (inputData != null && !inputData.isEmpty()) {
            Path inputFile = tempDir.resolve("input.txt");
            try (BufferedWriter bw = Files.newBufferedWriter(inputFile)) {
                bw.write(String.valueOf(inputData.size())); bw.newLine();
                for (int v : inputData) bw.write(v + "\n");
            }
        }
    }

    private boolean compile(Path dir, String className, boolean docker, ExecutionResult result)
            throws IOException, InterruptedException {
        Process compileProcess = docker
                ? createDockerCompileProcess(dir, className)
                : createDirectCompileProcess(dir, className);
        String compileOutput = readOutput(compileProcess);
        boolean compiled = compileProcess.waitFor(EXECUTION_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                && compileProcess.exitValue() == 0;
        if (!compiled) {
            if (compileProcess.isAlive()) {
                compileProcess.destroyForcibly();
                result.setResult(ExecutionResult.Result.TIMEOUT);
            } else {
                result.setResult(ExecutionResult.Result.COMPILATION_ERROR);
                if (!compileOutput.isBlank()) Collections.addAll(result.getOutputLines(), compileOutput.split("\n"));
            }
        }
        return compiled;
    }

    private Process createDirectCompileProcess(Path dir, String className) throws IOException {
        return new ProcessBuilder("javac", className + ".java")
                .directory(dir.toFile())
                .redirectErrorStream(true)
                .start();
    }

    private Process createDockerCompileProcess(Path tempDir, String className) throws IOException {
        List<String> cmd = new ArrayList<>(Arrays.asList(
                "docker", "run", "--rm",
                "--memory=" + dockerMemoryLimit, "--memory-swap=" + dockerMemoryLimit,
                "--cpus=" + dockerCpuLimit,
                "--network=none",
                "--read-only",
                "--tmpfs", "/tmp:exec,size=50m,mode=1777",
                "--security-opt", "no-new-privileges",
                "--cap-drop=ALL",
                "--pids-limit=16",
                "--ulimit", "nofile=128:128",
                "-v", tempDir.toAbsolutePath() + ":/workspace",
                "-w", "/workspace",
                "openjdk:11-jdk-slim",
                "sh", "-c", "timeout " + EXECUTION_TIMEOUT_SECONDS + "s javac " + className + ".java"
        ));
        String seccomp = getSeccompProfile();
        if (seccomp != null) cmd.addAll(Arrays.asList("--security-opt", "seccomp=" + seccomp));
        return new ProcessBuilder(cmd).redirectErrorStream(true).start();
    }

    private void run(Path dir, String className, List<Integer> inputData, boolean docker, ExecutionResult result)
            throws IOException, InterruptedException {
        Process runProcess = docker
                ? createDockerRunProcess(dir, className, inputData)
                : createDirectRunProcess(dir, className, inputData);
        ExecutionContext context = new ExecutionContext();
        startTimeoutWatcher(runProcess, context);
        String runOutput = readOutput(runProcess);
        runProcess.waitFor();
        if (context.wasKilledByTimeout) {
            result.setResult(ExecutionResult.Result.TIMEOUT);
            return;
        }
        if (runProcess.isAlive()) {
            runProcess.destroyForcibly();
            result.setResult(ExecutionResult.Result.TIMEOUT);
            return;
        }
        if (runProcess.exitValue() != 0) {
            result.setResult(ExecutionResult.Result.RUNTIME_ERROR);
            if (!runOutput.isBlank()) Collections.addAll(result.getOutputLines(), runOutput.split("\n"));
            return;
        }
        List<String> lines = new ArrayList<>();
        for (String l : runOutput.split("\n")) if (!l.isBlank()) lines.add(l.trim());
        result.setOutputLines(lines);
        result.setResult(ExecutionResult.Result.SUCCESS);
    }

    private Process createDirectRunProcess(Path dir, String className, List<Integer> inputData) throws IOException {
        Process p = new ProcessBuilder("java", className)
                .directory(dir.toFile())
                .redirectErrorStream(true)
                .start();
        if (inputData != null && !inputData.isEmpty()) {
            try (PrintWriter writer = new PrintWriter(p.getOutputStream(), true)) {
                writer.println(inputData.size());
                inputData.forEach(writer::println);
            }
        } else {
            p.getOutputStream().close();
        }
        return p;
    }

    private Process createDockerRunProcess(Path tempDir, String className, List<Integer> inputData) throws IOException {
        List<String> cmd = new ArrayList<>(Arrays.asList(
                "docker", "run", "--rm",
                "--memory=" + dockerMemoryLimit, "--memory-swap=" + dockerMemoryLimit,
                "--cpus=" + dockerCpuLimit,
                "--network=none",
                "--read-only",
                "--tmpfs", "/tmp:exec,size=10m,mode=1777",
                "--security-opt", "no-new-privileges",
                "--cap-drop=ALL",
                "--pids-limit=32",
                "--ulimit", "nofile=64:64",
                "--ulimit", "nproc=16:16",
                "-v", tempDir.toAbsolutePath() + ":/workspace:ro",
                "-w", "/tmp",
                "openjdk:11-jre-slim",
                "sh", "-c",
                "cp /workspace/" + className + ".class /tmp/ && " +
                        "timeout " + EXECUTION_TIMEOUT_SECONDS + "s java " + className +
                        (inputData != null && !inputData.isEmpty() ? " < /workspace/input.txt" : "")
        ));
        String sec = getSeccompProfile();
        if (sec != null) cmd.addAll(Arrays.asList("--security-opt", "seccomp=" + sec));
        return new ProcessBuilder(cmd).redirectErrorStream(true).start();
    }

    private String readOutput(Process process) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) output.append(line).append("\n");
        }
        return output.toString();
    }

    private void startTimeoutWatcher(Process process, ExecutionContext context) {
        Thread killer = new Thread(() -> {
            try {
                Thread.sleep(EXECUTION_TIMEOUT_SECONDS * 1000);
                if (process.isAlive()) { context.wasKilledByTimeout = true; process.destroyForcibly(); }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        killer.setDaemon(true);
        killer.start();
    }

    private boolean isDockerAvailable() {
        try {
            Process p = new ProcessBuilder("docker", "--version").start();
            boolean ok = p.waitFor(5, TimeUnit.SECONDS) && p.exitValue() == 0;
            return ok;
        } catch (Exception e) {
            return false;
        }
    }

    private void cleanup(Path tempDir) {
        try {
            if (Files.exists(tempDir)) {
                Files.walk(tempDir)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        } catch (IOException e) {
            System.err.println("Error cleaning up " + tempDir + ": " + e.getMessage());
        }
    }

    private String getSeccompProfile() {
        if (seccompProfilePath == null || seccompProfilePath.isBlank()) return null;
        Path p = Paths.get(seccompProfilePath.trim());
        if (!p.isAbsolute()) p = Paths.get(System.getProperty("user.dir")).resolve(p).normalize();
        return p.toAbsolutePath().toString();
    }

    private static class ExecutionContext { boolean wasKilledByTimeout; }
}
