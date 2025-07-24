package com.coderacer.controller;

import com.coderacer.dto.CodingProblemDTO;
import com.coderacer.dto.CodingProblemRequestDTO;
import com.coderacer.enums.Difficulty;
import com.coderacer.service.CodingProblemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/problems")
public class CodingProblemController {

    private final CodingProblemService codingProblemService;

    @Autowired
    public CodingProblemController(CodingProblemService codingProblemService) {
        this.codingProblemService = codingProblemService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CodingProblemDTO>> getAllProblems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CodingProblemDTO> problems = codingProblemService.getAllProblems(pageable);
        return ResponseEntity.ok(problems);
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<CodingProblemDTO> getProblemById(@PathVariable UUID id) {
        CodingProblemDTO problem = codingProblemService.getProblemById(id);
        return ResponseEntity.ok(problem);
    }

    @GetMapping("/difficulty/{difficulty}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CodingProblemDTO>> getProblemsByDifficulty(
            @PathVariable Difficulty difficulty,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<CodingProblemDTO> problems = codingProblemService.getProblemsByDifficulty(difficulty, pageable);
        return ResponseEntity.ok(problems);
    }

    @GetMapping("/search")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Page<CodingProblemDTO>> searchProblemsByTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<CodingProblemDTO> problems = codingProblemService.searchProblemsByTitle(title, pageable);
        return ResponseEntity.ok(problems);
    }

    @GetMapping("/random")
    @PreAuthorize("permitAll()")
    public ResponseEntity<CodingProblemDTO> getRandomProblem() {
        CodingProblemDTO problem = codingProblemService.getRandomProblem();
        return ResponseEntity.ok(problem);
    }

    @GetMapping("/random/difficulty/{difficulty}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<CodingProblemDTO> getRandomProblemByDifficulty(@PathVariable Difficulty difficulty) {
        CodingProblemDTO problem = codingProblemService.getRandomProblemByDifficulty(difficulty);
        return ResponseEntity.ok(problem);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CodingProblemDTO> createProblem(@Valid @RequestBody CodingProblemRequestDTO requestDTO) {
        CodingProblemDTO createdProblem = codingProblemService.createProblem(requestDTO);
        return new ResponseEntity<>(createdProblem, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CodingProblemDTO> updateProblem(
            @PathVariable UUID id,
            @Valid @RequestBody CodingProblemRequestDTO requestDTO) {
        CodingProblemDTO updatedProblem = codingProblemService.updateProblem(id, requestDTO);
        return ResponseEntity.ok(updatedProblem);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProblem(@PathVariable UUID id) {
        codingProblemService.deleteProblem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getProblemsCountByDifficulty(@RequestParam Difficulty difficulty) {
        long count = codingProblemService.getProblemsCountByDifficulty(difficulty);
        return ResponseEntity.ok(count);
    }
}