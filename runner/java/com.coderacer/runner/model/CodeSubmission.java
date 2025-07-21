package com.coderacer.runner.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    /** The raw Java source code submitted by the user */
    @Lob
    @Column(nullable = false)
    private String code = "";

    /**
     * The expected output lines for this submission.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "submission_expected_outputs",
            joinColumns = @JoinColumn(name = "submission_id")
    )
    @Column(nullable = false)
    private List<String> expectedOutputs = new ArrayList<>();
}
