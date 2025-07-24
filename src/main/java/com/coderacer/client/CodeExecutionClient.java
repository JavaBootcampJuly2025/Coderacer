package com.coderacer.client;

import com.coderacer.dto.ExecutionResultDTO;
import com.coderacer.exception.CodeExecutionClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
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
     *
     * @param code        The source code to execute
     * @param inputToPass The parameter list in the form of a string
     * @return ExecutionResult containing output, errors, and execution info
     */
    public ExecutionResultDTO executeCode(String code, List<Integer> inputToPass) {
        try {
            String url = codeExecutionServiceUrl + "/api/code/execute";

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> payload = new HashMap<>();
            payload.put("code", code);
            payload.put("inputData", inputToPass);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            // Make the call
            log.debug("Calling code execution service at: {}", url);
            ResponseEntity<ExecutionResultDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    ExecutionResultDTO.class
            );

            return response.getBody();

        } catch (RestClientException e) {
            log.error("Error calling code execution service: {}", e.getMessage());
            throw new CodeExecutionClientException("Unexpected error during code execution", e);
        } catch (Exception e) {
            log.error("Unexpected error during code execution: {}", e.getMessage());
            throw new CodeExecutionClientException("Unexpected error during code execution", e);
        }
    }
}