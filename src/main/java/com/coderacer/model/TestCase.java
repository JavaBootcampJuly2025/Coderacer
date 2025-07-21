package com.coderacer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Embeddable type (absolutely depends on CodingProblem) to represent one test case (inputs and corresponding expected outputs).
 */
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TestCase {
    @ElementCollection
    @Column(name = "input_line", length = 200)
    private List<@NotBlank @Size(max = 200) String> inputs = new ArrayList<>();

    @ElementCollection
    @Column(name = "output_line", length = 200)
    private List<@NotBlank @Size(max = 200) String> expectedOutputs = new ArrayList<>();
}
