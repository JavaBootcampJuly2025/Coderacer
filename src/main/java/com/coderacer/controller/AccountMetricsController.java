package com.coderacer.controller;

import com.coderacer.dto.GameplayMetricsDTO;
import com.coderacer.dto.SessionLookupParametersDTO;
import com.coderacer.service.AccountMetricsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/metrics")
public class AccountMetricsController {
    private final AccountMetricsService accountMetricsService;

    @GetMapping("/gameplayMetrics/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    public ResponseEntity<GameplayMetricsDTO> getAccountMetrics(@PathVariable UUID id) {
        return ResponseEntity.ok(accountMetricsService.getAccountMetrics(id));
    }

    @GetMapping("/gameplayMetricsWithParameters/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    public ResponseEntity<GameplayMetricsDTO> getAccountMetricsWithParameters(
            @PathVariable UUID id,
            @RequestBody @Valid SessionLookupParametersDTO requestDTO) {
        return ResponseEntity.ok(accountMetricsService.getAccountMetrics(id, requestDTO));
    }
}
