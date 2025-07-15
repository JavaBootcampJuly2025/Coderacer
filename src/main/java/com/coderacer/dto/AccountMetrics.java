package com.coderacer.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class AccountMetrics {
    private UUID id;
//    private Account account;
    private double avgCpm;
    private double avgAccuracy;
    private double avgCpmWeekly;
}
