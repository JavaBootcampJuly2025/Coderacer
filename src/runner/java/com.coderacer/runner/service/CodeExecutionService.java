package com.coderacer.runner.service;

import com.coderacer.runner.model.ExecutionResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Service responsible for compiling and executing Java code strings using Docker containers.
 */
@Service
public class CodeExecutionService {

    private static final long EXECUTION_TIMEOUT_SECONDS = 10;

    @Value("${code.execution.use-docker:true}")
    private boolean useDocker;

    @Value("${code.execution.docker.memory:64m}")
    private String dockerMemoryLimit;

    @Value("${code.execution.docker.cpu:0.2}")
    private String dockerCpuLimit;

    public ExecutionResult compileAndRun(String javaCode, List<Integer> inputData) {
        if (useDocker && isDockerAvailable()) {
            return executeCode(javaCode, inputData, true);
        } else {
            return executeCode(javaCode, inputData, false);
        }
    }

    public ExecutionResult compileAndRun(String javaCode) {
        return compileAndRun(javaCode, null);
    }

    private ExecutionResult executeCode(String javaCode, List<Integer> inputData, boolean withDocker) {
        ExecutionResult result = new ExecutionResult();
        String uniqueId = UUID.randomUUID().toString().replace("-", "");
        String className = "GeneratedClass_" + uniqueId;
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "java_code_exec_" + uniqueId);

        try {
            // Setup phase
            setupExecutionEnvironment(tempDir, className, javaCode, inputData);

            // Compilation phase
            if (!compileCode(tempDir, className, withDocker, result)) {
                return result;
            }

            // Execution phase
            executeCompiledCode(tempDir, className, inputData, withDocker, result);

        } catch (IOException | InterruptedException e) {
            result.setResult(ExecutionResult.Result.RUNTIME_ERROR);
            result.getOutputLines().add((withDocker ? "Docker" : "Direct") + " execution error: " + e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            cleanupTempDirectory(tempDir);
        }

        return result;
    }

    private void setupExecutionEnvironment(Path tempDir, String className, String javaCode, List<Integer> inputData) throws IOException {
        Files.createDirectories(tempDir);

        // Write Java code
        Path javaFilePath = tempDir.resolve(className + ".java");
        String modifiedJavaCode = javaCode.replaceFirst("public\\s+class\\s+\\w+", "public class " + className);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(javaFilePath.toFile()))) {
            writer.write(modifiedJavaCode);
        }

        // Write input data if available (only needed for Docker)
        if (inputData != null && !inputData.isEmpty()) {
            Path inputFilePath = tempDir.resolve("input.txt");
            try (BufferedWriter inputWriter = new BufferedWriter(new FileWriter(inputFilePath.toFile()))) {
                inputWriter.write(String.valueOf(inputData.size()));
                inputWriter.newLine();
                for (Integer value : inputData) {
                    inputWriter.write(String.valueOf(value));
                    inputWriter.newLine();
                }
            }
        }
    }

    private boolean compileCode(Path tempDir, String className, boolean withDocker, ExecutionResult result) throws IOException, InterruptedException {
        Process compileProcess = withDocker
                ? createDockerCompileProcess(tempDir, className)
                : createDirectCompileProcess(tempDir, className);

        return handleCompilationResult(compileProcess, result);
    }

    private Process createDockerCompileProcess(Path tempDir, String className) throws IOException {
        return new ProcessBuilder(
                "docker", "run", "--rm",
                "--memory=256m", "--memory-swap=256m",
                "--cpus=0.5",
                "--network=none",
                "--read-only",
                "--tmpfs", "/tmp:exec,size=50m,mode=1777",
                "--security-opt", "no-new-privileges",
                "--cap-drop=ALL",
                "--pids-limit=16",
                "--ulimit", "nofile=128:128",
                "-v", tempDir.toAbsolutePath() + ":/workspace", // Keep writable for compilation
                "-w", "/workspace",
                "openjdk:11-jdk-slim",
                "sh", "-c", "timeout " + EXECUTION_TIMEOUT_SECONDS + "s javac " + className + ".java"
        ).redirectErrorStream(true).start();
    }

    private Process createDirectCompileProcess(Path tempDir, String className) throws IOException {
        return new ProcessBuilder("javac", className + ".java")
                .directory(tempDir.toFile())
                .redirectErrorStream(true)
                .start();
    }

    private boolean handleCompilationResult(Process compileProcess, ExecutionResult result) throws IOException, InterruptedException {
        String compileOutput = readProcessOutput(compileProcess);
        boolean compiled = compileProcess.waitFor(EXECUTION_TIMEOUT_SECONDS, TimeUnit.SECONDS) && compileProcess.exitValue() == 0;

        if (!compiled) {
            if (compileProcess.isAlive()) {
                compileProcess.destroyForcibly();
                result.setResult(ExecutionResult.Result.TIMEOUT);
            } else {
                result.setResult(ExecutionResult.Result.COMPILATION_ERROR);
                if (!compileOutput.trim().isEmpty()) {
                    result.getOutputLines().addAll(Arrays.asList(compileOutput.split("\n")));
                }
            }
        }
        return compiled;
    }

    private void executeCompiledCode(Path tempDir, String className, List<Integer> inputData, boolean withDocker, ExecutionResult result) throws IOException, InterruptedException {
        Process runProcess = withDocker
                ? createDockerRunProcess(tempDir, className, inputData)
                : createDirectRunProcess(tempDir, className, inputData);

        ExecutionContext context = new ExecutionContext();
        setupTimeoutKiller(runProcess, context);

        String runOutput = readProcessOutput(runProcess);
        waitForProcess(runProcess);

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
            if (!runOutput.trim().isEmpty()) {
                result.getOutputLines().addAll(Arrays.asList(runOutput.split("\n")));
            }
            return;
        }

        // Success case
        processSuccessfulOutput(runOutput, result);
    }

    private Process createDockerRunProcess(Path tempDir, String className, List<Integer> inputData) throws IOException {
        List<String> runCommand = new ArrayList<>(Arrays.asList(
                "docker", "run", "--rm",
                "--memory=" + dockerMemoryLimit,
                "--memory-swap=" + dockerMemoryLimit, // Prevent swap usage
                "--cpus=" + dockerCpuLimit,
                "--network=none", // No network access
                "--read-only", // Read-only root filesystem
                "--tmpfs", "/tmp:exec,size=10m,mode=1777", // Limited tmp space
                "--security-opt", "no-new-privileges", // Prevent privilege escalation
                "--cap-drop=ALL", // Drop all capabilities
                "--pids-limit=32", // Limit number of processes
                "--ulimit", "nofile=64:64", // Limit open files
                "--ulimit", "nproc=16:16", // Limit processes
                "-v", tempDir.toAbsolutePath() + ":/workspace:ro", // Read-only mount
                "-w", "/tmp" // Set working directory to writable tmp
        ));

        if (inputData != null && !inputData.isEmpty()) {
            runCommand.addAll(Arrays.asList(
                    "openjdk:11-jre-slim",
                    "sh", "-c",
                    "cp /workspace/" + className + ".class /tmp/ && " +
                            "timeout " + EXECUTION_TIMEOUT_SECONDS + "s java " + className + " < /workspace/input.txt"
            ));
        } else {
            runCommand.addAll(Arrays.asList(
                    "openjdk:11-jre-slim",
                    "sh", "-c",
                    "cp /workspace/" + className + ".class /tmp/ && " +
                            "timeout " + EXECUTION_TIMEOUT_SECONDS + "s java " + className
            ));
        }

        return new ProcessBuilder(runCommand).redirectErrorStream(true).start();
    }

    private Process createDirectRunProcess(Path tempDir, String className, List<Integer> inputData) throws IOException {
        Process runProcess = new ProcessBuilder("java", className)
                .directory(tempDir.toFile())
                .redirectErrorStream(true)
                .start();

        // Handle input for direct execution
        if (inputData != null && !inputData.isEmpty()) {
            try (PrintWriter writer = new PrintWriter(runProcess.getOutputStream(), true)) {
                writer.println(inputData.size());
                for (Integer value : inputData) {
                    writer.println(value);
                }
            } catch (Exception ignored) {
                // Continue if input writing fails
            }
        } else {
            try {
                runProcess.getOutputStream().close();
            } catch (IOException ignored) {
            }
        }

        return runProcess;
    }

    private void setupTimeoutKiller(Process process, ExecutionContext context) {
        Thread killerThread = new Thread(() -> {
            try {
                Thread.sleep(EXECUTION_TIMEOUT_SECONDS * 1000);
                if (process.isAlive()) {
                    context.wasKilledByTimeout = true;
                    process.destroyForcibly();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        killerThread.setDaemon(true);
        killerThread.start();
    }

    private void waitForProcess(Process process) throws InterruptedException {
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        }
    }

    private void processSuccessfulOutput(String runOutput, ExecutionResult result) {
        List<String> actualOutputLines = new ArrayList<>();
        if (!runOutput.trim().isEmpty()) {
            String[] lines = runOutput.split("\n");
            for (String line : lines) {
                String trimmedLine = line.trim();
                if (!trimmedLine.isEmpty()) {
                    actualOutputLines.add(trimmedLine);
                }
            }
        }
        result.setOutputLines(actualOutputLines);
        result.setResult(ExecutionResult.Result.SUCCESS);
    }

    private boolean isDockerAvailable() {
        try {
            Process process = new ProcessBuilder("docker", "--version").start();
            boolean finished = process.waitFor(5, TimeUnit.SECONDS);
            return finished && process.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private void cleanupTempDirectory(Path tempDir) {
        try {
            if (Files.exists(tempDir)) {
                Files.walk(tempDir)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        } catch (IOException e) {
            System.err.println("Error during cleanup of " + tempDir + ": " + e.getMessage());
        }
    }

    private String readProcessOutput(Process process) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        return output.toString();
    }

    // Helper class to track timeout state
    private static class ExecutionContext {
        boolean wasKilledByTimeout = false;
    }
}