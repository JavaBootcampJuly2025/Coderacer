package com.coderacer.runner.service;

import com.coderacer.runner.model.ExecutionResult;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Service responsible for compiling and executing Java code strings.
 */
@Service
public class CodeExecutionService {

    private static final long EXECUTION_TIMEOUT_SECONDS = 10; // Timeout for compilation and execution

    /**
     * Compiles and executes the given Java code string with optional input data.
     *
     * @param javaCode        The Java code as a string. It must contain a public class with a main method.
     * @param inputData       Optional list of integers to feed to Scanner input
     * @return ExecutionResult containing the result status and actual output lines
     */
    public ExecutionResult compileAndRun(String javaCode, List<Integer> inputData) {
        ExecutionResult result = new ExecutionResult();

        // Generate a unique identifier for this execution to create isolated files
        String uniqueId = UUID.randomUUID().toString().replace("-", "");
        String className = "GeneratedClass_" + uniqueId;
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "java_code_exec_" + uniqueId);
        Path javaFilePath = tempDir.resolve(className + ".java");

        try {
            // 1. Create a temporary directory
            Files.createDirectories(tempDir);

            // 2. Write the Java code to a .java file
            // Ensure the class name in the code matches the file name
            String modifiedJavaCode = javaCode.replaceFirst("public\\s+class\\s+\\w+", "public class " + className);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(javaFilePath.toFile()))) {
                writer.write(modifiedJavaCode);
            }

            // 3. Compile the Java code
            Process compileProcess = new ProcessBuilder("javac", javaFilePath.toString())
                    .directory(tempDir.toFile()) // Set working directory for javac
                    .redirectErrorStream(true) // Merge stderr into stdout
                    .start();

            String compileOutput = readProcessOutput(compileProcess);
            boolean compiled = compileProcess.waitFor(EXECUTION_TIMEOUT_SECONDS, TimeUnit.SECONDS) && compileProcess.exitValue() == 0;

            if (!compiled) {
                if (compileProcess.isAlive()) {
                    compileProcess.destroyForcibly();
                    result.setResult(ExecutionResult.Result.TIMEOUT);
                } else {
                    result.setResult(ExecutionResult.Result.COMPILATION_ERROR);
                    // Add compilation error to output lines
                    if (!compileOutput.trim().isEmpty()) {
                        result.getOutputLines().addAll(Arrays.asList(compileOutput.split("\n")));
                    }
                }
                return result;
            }

            // 4. Run the compiled Java code
            Process runProcess = new ProcessBuilder("java", className)
                    .directory(tempDir.toFile())
                    .redirectErrorStream(true)
                    .start();

            // Provide input data to the process if available
            if (inputData != null && !inputData.isEmpty()) {
                try (PrintWriter writer = new PrintWriter(runProcess.getOutputStream(), true)) {
                    // First write the count of integers
                    writer.println(inputData.size());
                    // Then write each integer
                    for (Integer value : inputData) {
                        writer.println(value);
                    }
                } catch (Exception e) {
                    // If we can't write input, continue anyway
                }
            } else {
                // Close stdin to prevent hanging on input operations
                try {
                    runProcess.getOutputStream().close();
                } catch (IOException ignored) {
                }
            }

            // Track if we killed the process due to timeout
            final boolean[] wasKilled = {false};

            // Create a killer thread that will force-terminate the process
            Thread killerThread = new Thread(() -> {
                try {
                    Thread.sleep(EXECUTION_TIMEOUT_SECONDS * 1000);
                    if (runProcess.isAlive()) {
                        wasKilled[0] = true; // Mark that we're killing due to timeout
                        runProcess.destroyForcibly();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            killerThread.setDaemon(true);
            killerThread.start();

            // Read output and wait for process
            String runOutput = readProcessOutput(runProcess);

            try {
                runProcess.waitFor(); // No timeout here - let the killer thread handle it
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Check if we killed the process due to timeout
            if (wasKilled[0]) {
                result.setResult(ExecutionResult.Result.TIMEOUT);
                return result;
            }

            // Check if process is somehow still alive (shouldn't happen but just in case)
            if (runProcess.isAlive()) {
                runProcess.destroyForcibly();
                result.setResult(ExecutionResult.Result.TIMEOUT);
                return result;
            }

            // Check if execution had runtime errors (only if not killed by us)
            if (runProcess.exitValue() != 0) {
                result.setResult(ExecutionResult.Result.RUNTIME_ERROR);
                if (!runOutput.trim().isEmpty()) {
                    result.getOutputLines().addAll(Arrays.asList(runOutput.split("\n")));
                }
                return result;
            }

            // Process successful execution output
            List<String> actualOutputLines = new ArrayList<>();
            if (!runOutput.trim().isEmpty()) {
                String[] lines = runOutput.split("\n");
                for (String line : lines) {
                    // Filter out empty lines and trim whitespace
                    String trimmedLine = line.trim();
                    if (!trimmedLine.isEmpty()) {
                        actualOutputLines.add(trimmedLine);
                    }
                }
            }

            result.setOutputLines(actualOutputLines);

            // If no undesirable results so far, assume success
            result.setResult(ExecutionResult.Result.SUCCESS);

        } catch (IOException | InterruptedException e) {
            result.setResult(ExecutionResult.Result.RUNTIME_ERROR);
            result.getOutputLines().add("Internal error: " + e.getMessage());
            Thread.currentThread().interrupt(); // Restore the interrupted status
        } finally {
            // 5. Clean up temporary files and directory
            cleanupTempDirectory(tempDir);
        }

        return result;
    }

    /**
     * Legacy method for backward compatibility
     */
    public ExecutionResult compileAndRun(String javaCode) {
        return compileAndRun(javaCode, null);
    }

    /**
     * Clean up temporary directory and its contents
     */
    private void cleanupTempDirectory(Path tempDir) {
        try {
            if (Files.exists(tempDir)) {
                Files.walk(tempDir)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        } catch (IOException e) {
            // Log cleanup error but don't throw - cleanup failures shouldn't affect the result
            System.err.println("Error during cleanup of " + tempDir + ": " + e.getMessage());
        }
    }

    /**
     * Reads the entire output stream of a process.
     *
     * @param process The process whose output stream is to be read.
     * @return The content of the process's output stream as a string.
     * @throws IOException If an I/O error occurs.
     */
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
}