package com.coderacer.dto;

import com.coderacer.model.LevelSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LevelSessionDto {
    private UUID levelId;
    private UUID accountId;
    private double cpm;
    private double accuracy;

    public static LevelSessionDto fromEntity(LevelSession session) {
        return new LevelSessionDto(
                session.getLevel().getId(),
                session.getAccount().getId(),
                session.getCpm(),
                session.getAccuracy()
        );
    }
}
