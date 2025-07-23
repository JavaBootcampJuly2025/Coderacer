package com.coderacer.dto;

import com.coderacer.model.LevelSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LevelSessionDTO {
    private UUID levelId;
    private UUID accountId;
    private double cpm;
    private double accuracy;

    public static LevelSessionDTO fromEntity(LevelSession session) {
        return new LevelSessionDTO(
                session.getLevel().getId(),
                session.getAccount().getId(),
                session.getCpm(),
                session.getAccuracy()
        );
    }
}
