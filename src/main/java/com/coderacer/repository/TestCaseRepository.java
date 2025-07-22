package com.coderacer.repository;

import com.coderacer.model.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
    List<TestCase> findByCodingProblem_Id(UUID codingProblemId);
}