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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(length = 2000)
    @NotBlank
    @Size(min = 100, max = 2000)
    private String codeSnippet;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @NotNull
    private ProgrammingLanguage language;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @NotNull
    private Difficulty difficulty;

    @ElementCollection
    @Column(length = 20)
    @Size(max = 10)
    @NotEmpty
    private List<@NotBlank @Size(max = 20) @Pattern(regexp = "^[a-zA-Z]+$") String> tags = new ArrayList<>();
}