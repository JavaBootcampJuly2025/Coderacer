package com.coderacer.model;

import com.coderacer.enums.Difficulty;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a coding problem, with multiple test cases.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "title"))
public class CodingProblem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @NotBlank
    @Size(min = 10, max = 100)
    @Column(nullable = false, length = 200)
    private String title;

    @NotBlank
    @Size(min = 30, max = 5000)
    @Column(nullable = false, length = 5000)
    private String description;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false, length = 20)
    private Difficulty difficulty;

    @ElementCollection
    @Size(max = 100)
    @Column(length = 200)
    private List<@NotBlank @Size(max = 200) String> exampleInputs = new ArrayList<>();

    @ElementCollection
    @Size(max = 100)
    @Column(length = 200)
    private List<@NotBlank @Size(max = 200) String> exampleOutputs = new ArrayList<>();

    /**
     * A list of test cases, each containing a list of inputs and a list of expected outputs.
     */
    @OneToMany(mappedBy = "codingProblem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<@Valid TestCase> testCases = new ArrayList<>();
}
