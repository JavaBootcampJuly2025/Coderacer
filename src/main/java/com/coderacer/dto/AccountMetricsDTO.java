package com.coderacer.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Setter
@Getter
public class AccountMetricsDTO {
//    private Account account;
    private double avgCpm;
    private double avgAccuracy;
    private double avgCpmWeekly;
    private double avgAccuracyWeekly;
}
