package com.coderacer.model;

import com.coderacer.enums.Difficulty;
import com.coderacer.enums.ProgrammingLanguage;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "code_snippet"))
@Entity
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;


    @NotBlank
    @Size(min = 100, max = 2000)
    @Column(nullable = false, length = 2000)
    private String codeSnippet;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false, length = 20)
    private ProgrammingLanguage language = ProgrammingLanguage.JAVA;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false, length = 20)
    private Difficulty difficulty = Difficulty.EASY;

    @ElementCollection
    @Size(max = 10)
    @Column(length = 20)
    private List<@NotBlank @Size(max = 20) @Pattern(regexp = "^[a-zA-Z]+$") String> tags = new ArrayList<>();
}