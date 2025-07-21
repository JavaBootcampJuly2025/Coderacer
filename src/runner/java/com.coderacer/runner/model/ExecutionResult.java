package com.coderacer.runner.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionResult {
    
    public enum Result {
        SUCCESS,
        OUTPUT_MISMATCH,
        COMPILATION_ERROR,
        RUNTIME_ERROR,
        TIMEOUT
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    /** Overall status of this run */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Result result = Result.TIMEOUT;

    /**
     * The actual lines of output produced by the user’s code.
     * Compare this against CodeSubmission.expectedOutputs.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private List<String> outputLines = new ArrayList<>();
}
