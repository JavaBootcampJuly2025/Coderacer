package com.coderacer.client;

import com.coderacer.dto.ExecutionResultDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Client to communicate with the code execution microservice
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CodeExecutionClient {

    private final RestTemplate restTemplate;

    @Value("${code-execution.service.base-url}")
    private String codeExecutionServiceUrl;

    /**
     * Calls the code execution microservice to compile and run code
     * @param code The source code to execute
     * @param input The input string to pass to the program
     * @return ExecutionResult containing output, errors, and execution info
     */
    public ExecutionResultDto executeCode(String code, String input) {
        try {
            String url = codeExecutionServiceUrl + "/api/code";

            // Prepare request body
            Map<String, String> requestBody = Map.of(
                    "code", code,
                    "input", input != null ? input : ""
            );

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

            // Make the call
            log.debug("Calling code execution service at: {}", url);
            ResponseEntity<ExecutionResultDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    ExecutionResultDto.class
            );

            ExecutionResultDto result = response.getBody();
            log.debug("Code execution completed. Success: {}", result != null ? result.isSuccess() : false);

            return result != null ? result : createErrorResult("Empty response from code execution service");

        } catch (RestClientException e) {
            log.error("Error calling code execution service: {}", e.getMessage());
            return createErrorResult("Service communication error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during code execution: {}", e.getMessage());
            return createErrorResult("Unexpected error: " + e.getMessage());
        }
    }

    private ExecutionResultDto createErrorResult(String error) {
        ExecutionResultDto result = new ExecutionResultDto();
        result.setSuccess(false);
        result.setError(error);
        result.setOutput("");
        result.setExecutionTimeMs(0L);
        return result;
    }
}