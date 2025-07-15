package com.coderacer.controller;

import com.coderacer.service.AccountMetricsService;
import com.coderacer.service.AccountMetricsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metrics")
public class AccountMetricsController {
    private AccountMetricsService accountMetricsService = new AccountMetricsServiceImpl();

    @GetMapping("/get/{id}")
    public void getAccountMetricsById(@PathVariable("id") Long id) {
        accountMetricsService.getAccountMetricsById(id);
    }
}
