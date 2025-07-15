package com.coderacer.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for creating a new LevelSession.
 * This separates the API input from the LevelSession entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LevelSessionCreateDto {
    private UUID levelId;
    private UUID accountId;
    private double cpm;
    private double accuracy;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
