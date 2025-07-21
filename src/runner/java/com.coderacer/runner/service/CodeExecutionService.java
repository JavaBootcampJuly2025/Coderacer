package com.coderacer.runner.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Service responsible for compiling and executing Java code strings.
 */
@Service
public class CodeExecutionService {

    private static final long EXECUTION_TIMEOUT_SECONDS = 10; // Timeout for compilation and execution

    /**
     * Compiles and executes the given Java code string.
     *
     * @param javaCode The Java code as a string. It must contain a public class with a main method.
     * Example: "public class MyClass { public static void main(String[] args) { System.out.println(\"Hello from compiled code!\"); } }"
     * @return The combined standard output and standard error from compilation and execution.
     */
    public String compileAndRun(String javaCode) {
        // Generate a unique identifier for this execution to create isolated files
        String uniqueId = UUID.randomUUID().toString().replace("-", "");
        String className = "GeneratedClass_" + uniqueId;
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "java_code_exec_" + uniqueId);
        Path javaFilePath = tempDir.resolve(className + ".java");

        StringBuilder output = new StringBuilder();

        try {
            // 1. Create a temporary directory
            Files.createDirectories(tempDir);
            output.append("Created temporary directory: ").append(tempDir).append("\n");

            // 2. Write the Java code to a .java file
            // Ensure the class name in the code matches the file name
            String modifiedJavaCode = javaCode.replaceFirst("public\\s+class\\s+\\w+", "public class " + className);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(javaFilePath.toFile()))) {
                writer.write(modifiedJavaCode);
            }
            output.append("Wrote Java code to: ").append(javaFilePath).append("\n");

            // 3. Compile the Java code
            output.append("\n--- Compilation Output ---\n");
            Process compileProcess = new ProcessBuilder("javac", javaFilePath.toString())
                    .directory(tempDir.toFile()) // Set working directory for javac
                    .redirectErrorStream(true) // Merge stderr into stdout
                    .start();

            String compileOutput = readProcessOutput(compileProcess);
            output.append(compileOutput);

            boolean compiled = compileProcess.waitFor(EXECUTION_TIMEOUT_SECONDS, TimeUnit.SECONDS) && compileProcess.exitValue() == 0;

            if (!compiled) {
                output.append("\nCompilation FAILED or TIMED OUT.\n");
                if (!compileProcess.isAlive()) {
                    output.append("Exit Code: ").append(compileProcess.exitValue()).append("\n");
                } else {
                    output.append("Compilation process timed out.\n");
                    compileProcess.destroyForcibly();
                }
                return output.toString();
            }
            output.append("Compilation SUCCESSFUL.\n");

            // 4. Run the compiled Java code
            output.append("\n--- Execution Output ---\n");
            Process runProcess = new ProcessBuilder("java", className)
                    .directory(tempDir.toFile()) // Set working directory for java
                    .redirectErrorStream(true) // Merge stderr into stdout
                    .start();

            String runOutput = readProcessOutput(runProcess);
            output.append(runOutput);

            boolean executed = runProcess.waitFor(EXECUTION_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if (!executed) {
                output.append("\nExecution TIMED OUT.\n");
                runProcess.destroyForcibly();
            } else {
                output.append("Exit Code: ").append(runProcess.exitValue()).append("\n");
            }

        } catch (IOException | InterruptedException e) {
            output.append("\nAn internal error occurred: ").append(e.getMessage()).append("\n");
            Thread.currentThread().interrupt(); // Restore the interrupted status
        } finally {
            // 5. Clean up temporary files and directory
            output.append("\n--- Cleanup ---\n");
            try {
                if (Files.exists(tempDir)) {
                    // Use Files.walk to delete directory and its contents recursively
                    Files.walk(tempDir)
                            .sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                    output.append("Cleaned up temporary directory: ").append(tempDir).append("\n");
                }
            } catch (IOException e) {
                output.append("Error during cleanup: ").append(e.getMessage()).append("\n");
            }
        }

        return output.toString();
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
