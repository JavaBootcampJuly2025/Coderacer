package com.coderacer.controller;

import com.coderacer.models.AccountMetrics;
import com.coderacer.service.AccountMetricsService;
import com.coderacer.service.AccountMetricsServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/metrics")
public class AccountMetricsController {
    private AccountMetricsService accountMetricsService = new AccountMetricsServiceImpl();

    @GetMapping("/get/{id}")
    public ResponseEntity<AccountMetrics> getAccountMetricsById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(accountMetricsService.getAccountMetricsById(id));
    }
}
