package com.coderacer.dto;

import com.coderacer.enums.Difficulty;
import com.coderacer.model.CodingProblem;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CodingProblemRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(min = 10, max = 100, message = "Title must be between 10 and 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 30, max = 5000, message = "Description must be between 30 and 5000 characters")
    private String description;

    @NotNull(message = "Difficulty is required")
    private Difficulty difficulty;

    @Size(max = 100, message = "Maximum 100 example inputs allowed")
    private List<@NotBlank @Size(max = 200) String> exampleInputs;

    @Size(max = 100, message = "Maximum 100 example outputs allowed")
    private List<@NotBlank @Size(max = 200) String> exampleOutputs;

    private List<@NotNull Integer> inputs;
    private List<@NotNull Integer> outputs;

    public CodingProblem toEntity() {
        CodingProblem entity = new CodingProblem();
        entity.setTitle(this.title);
        entity.setDescription(this.description);
        entity.setDifficulty(this.difficulty);
        entity.setExampleInputs(this.exampleInputs != null ? new ArrayList<>(this.exampleInputs) : new ArrayList<>());
        entity.setExampleOutputs(this.exampleOutputs != null ? new ArrayList<>(this.exampleOutputs) : new ArrayList<>());
        entity.setInputs(this.inputs != null ? new ArrayList<>(this.inputs) : new ArrayList<>());
        entity.setOutputs(this.outputs != null ? new ArrayList<>(this.outputs) : new ArrayList<>());
        return entity;
    }

    public void updateEntity(CodingProblem entity) {
        if (this.title != null) entity.setTitle(this.title);
        if (this.description != null) entity.setDescription(this.description);
        if (this.difficulty != null) entity.setDifficulty(this.difficulty);
        if (this.exampleInputs != null) entity.setExampleInputs(new ArrayList<>(this.exampleInputs));
        if (this.exampleOutputs != null) entity.setExampleOutputs(new ArrayList<>(this.exampleOutputs));
        if (this.inputs != null) entity.setInputs(new ArrayList<>(this.inputs));
        if (this.outputs != null) entity.setOutputs(new ArrayList<>(this.outputs));
    }
}