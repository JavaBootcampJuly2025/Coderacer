package com.coderacer.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionResultDTO {

    public enum Result {
        SUCCESS,
        OUTPUT_MISMATCH,
        COMPILATION_ERROR,
        RUNTIME_ERROR,
        TIMEOUT
    }

    /** UUID of the run */
    private UUID id;

    /** Overall status of this run */
    private Result result;

    /**
     * The actual lines of output produced by the user’s code.
     * Compare this against CodeSubmission.expectedOutputs.
     */
    private List<String> outputLines;
}