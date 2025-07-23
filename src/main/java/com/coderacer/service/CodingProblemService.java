package com.coderacer.service;

import com.coderacer.dto.CodingProblemDTO;
import com.coderacer.dto.CodingProblemRequestDTO;
import com.coderacer.enums.Difficulty;
import com.coderacer.exception.CodingProblemNotFoundException;
import com.coderacer.model.CodingProblem;
import com.coderacer.repository.CodingProblemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class CodingProblemService {

    private final CodingProblemRepository codingProblemRepository;

    @Autowired
    public CodingProblemService(CodingProblemRepository codingProblemRepository) {
        this.codingProblemRepository = codingProblemRepository;
    }

    @Transactional(readOnly = true)
    public Page<CodingProblemDTO> getAllProblems(Pageable pageable) {
        Page<CodingProblem> problems = codingProblemRepository.findAll(pageable);
        return problems.map(CodingProblemDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public CodingProblemDTO getProblemById(UUID id) {
        CodingProblem problem = codingProblemRepository.findById(id)
                .orElseThrow(() -> new CodingProblemNotFoundException("Coding problem not found with id: " + id));
        return CodingProblemDTO.fromEntity(problem);
    }

    @Transactional(readOnly = true)
    public Page<CodingProblemDTO> getProblemsByDifficulty(Difficulty difficulty, Pageable pageable) {
        Page<CodingProblem> problems = codingProblemRepository.findByDifficulty(difficulty, pageable);
        return problems.map(CodingProblemDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<CodingProblemDTO> searchProblemsByTitle(String title, Pageable pageable) {
        Page<CodingProblem> problems = codingProblemRepository.findByTitleContainingIgnoreCase(title, pageable);
        return problems.map(CodingProblemDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public CodingProblemDTO getRandomProblem() {
        CodingProblem problem = codingProblemRepository.findRandom()
                .orElseThrow(() -> new CodingProblemNotFoundException("No coding problems available"));
        return CodingProblemDTO.fromEntity(problem);
    }

    @Transactional(readOnly = true)
    public CodingProblemDTO getRandomProblemByDifficulty(Difficulty difficulty) {
        CodingProblem problem = codingProblemRepository.findRandomByDifficulty(difficulty.name())
                .orElseThrow(() -> new CodingProblemNotFoundException(
                        "No coding problems available for difficulty: " + difficulty));
        return CodingProblemDTO.fromEntity(problem);
    }

    public CodingProblemDTO createProblem(CodingProblemRequestDTO requestDTO) {
        CodingProblem problem = requestDTO.toEntity();
        CodingProblem savedProblem = codingProblemRepository.save(problem);
        return CodingProblemDTO.fromEntity(savedProblem);
    }

    public CodingProblemDTO updateProblem(UUID id, CodingProblemRequestDTO requestDTO) {
        CodingProblem existingProblem = codingProblemRepository.findById(id)
                .orElseThrow(() -> new CodingProblemNotFoundException("Coding problem not found with id: " + id));

        requestDTO.updateEntity(existingProblem);

        CodingProblem updatedProblem = codingProblemRepository.save(existingProblem);
        return CodingProblemDTO.fromEntity(updatedProblem);
    }

    public void deleteProblem(UUID id) {
        if (!codingProblemRepository.existsById(id)) {
            throw new CodingProblemNotFoundException("Coding problem not found with id: " + id);
        }
        codingProblemRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long getProblemsCountByDifficulty(Difficulty difficulty) {
        return codingProblemRepository.countByDifficulty(difficulty);
    }

    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        return codingProblemRepository.existsById(id);
    }
}