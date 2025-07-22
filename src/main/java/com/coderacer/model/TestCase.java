package com.coderacer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ElementCollection
    @CollectionTable(name = "testcase_inputs", joinColumns = @JoinColumn(name = "testcase_id"))
    @Column(name = "input_line", length = 200)
    private List<@NotBlank @Size(max = 200) String> inputs = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "testcase_outputs", joinColumns = @JoinColumn(name = "testcase_id"))
    @Column(name = "output_line", length = 200)
    private List<@NotBlank @Size(max = 200) String> expectedOutputs = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private CodingProblem codingProblem;
}