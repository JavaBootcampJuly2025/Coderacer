package com.coderacer.controller;

import com.coderacer.dto.GameplayMetricsDTO;
import com.coderacer.dto.SessionLookupParametersDTO;
import com.coderacer.service.AccountMetricsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/metrics")
public class AccountMetricsController {
    private final AccountMetricsService accountMetricsService = new AccountMetricsService();

    @GetMapping("/gameplayMetrics/{id}")
    public ResponseEntity<GameplayMetricsDTO> getAccountMetrics(@PathVariable UUID id) {
        return ResponseEntity.ok(accountMetricsService.getAccountMetrics(id));
    }

    @GetMapping("/gameplayMetricsWithParameters/{id}")
    public ResponseEntity<GameplayMetricsDTO> getAccountMetricsWithParameters(
            @PathVariable UUID id,
            @RequestBody SessionLookupParametersDTO requestDTO) {
        return ResponseEntity.ok(accountMetricsService.getAccountMetrics(id, requestDTO));
    }
}
