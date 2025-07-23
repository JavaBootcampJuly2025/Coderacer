package com.coderacer.dto;

import com.coderacer.enums.Difficulty;
import com.coderacer.model.CodingProblem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CodingProblemDTO {
    private UUID id;
    private String title;
    private String description;
    private Difficulty difficulty;
    private List<String> exampleInputs;
    private List<String> exampleOutputs;
    private List<Integer> inputs;
    private List<Integer> outputs;

    public static CodingProblemDTO fromEntity(CodingProblem entity) {
        if (entity == null) return null;

        return new CodingProblemDTO(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getDifficulty(),
                entity.getExampleInputs() != null ? new ArrayList<>(entity.getExampleInputs()) : new ArrayList<>(),
                entity.getExampleOutputs() != null ? new ArrayList<>(entity.getExampleOutputs()) : new ArrayList<>(),
                entity.getInputs() != null ? new ArrayList<>(entity.getInputs()) : new ArrayList<>(),
                entity.getOutputs() != null ? new ArrayList<>(entity.getOutputs()) : new ArrayList<>()
        );
    }
}