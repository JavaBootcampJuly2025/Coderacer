package com.coderacer.repository;

import com.coderacer.enums.Difficulty;
import com.coderacer.model.CodingProblem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CodingProblemRepository extends JpaRepository<CodingProblem, UUID> {

    /**
     * Find coding problems by difficulty level
     */
    Page<CodingProblem> findByDifficulty(Difficulty difficulty, Pageable pageable);

    /**
     * Find coding problems by title containing text (case insensitive)
     */
    Page<CodingProblem> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    /**
     * Get a random coding problem by difficulty
     */
    @Query(value = "SELECT * FROM coding_problem WHERE difficulty = :difficulty ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<CodingProblem> findRandomByDifficulty(@Param("difficulty") String difficulty);

    /**
     * Get a completely random coding problem
     */
    @Query(value = "SELECT * FROM coding_problem ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<CodingProblem> findRandom();

    /**
     * Count problems by difficulty
     */
    long countByDifficulty(Difficulty difficulty);
}