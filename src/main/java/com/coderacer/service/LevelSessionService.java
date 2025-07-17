package com.coderacer.service;

import com.coderacer.model.Account;
import com.coderacer.model.Level;
import com.coderacer.model.LevelSession;
import com.coderacer.repository.AccountRepository;
import com.coderacer.repository.LevelRepository;
import com.coderacer.repository.LevelSessionRepository;
import com.coderacer.dto.LevelSessionCreateDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class LevelSessionService {
    private final LevelSessionRepository levelSessionRepository;
    private final LevelRepository levelRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public LevelSessionService(LevelSessionRepository levelSessionRepository,
                               LevelRepository levelRepository,
                               AccountRepository accountRepository) {
        this.levelSessionRepository = levelSessionRepository;
        this.levelRepository = levelRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * Creates a new level session from a DTO.
     *
     * @param createDto The DTO containing data for the new level session.
     * @return The newly created and saved LevelSession.
     * @throws EntityNotFoundException if the Level or Account does not exist.
     */
    @Transactional
    public LevelSession createLevelSession(LevelSessionCreateDto createDto) {
        // Fetch the Level and Account entities based on IDs from the DTO
        Level level = levelRepository.findById(createDto.getLevelId())
                .orElseThrow(() -> new EntityNotFoundException("Level not found with ID: " + createDto.getLevelId()));
        Account account = accountRepository.findById(createDto.getAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Account not found with ID: " + createDto.getAccountId()));

        LevelSession session = new LevelSession(
                level,
                account,
                createDto.getCpm(),
                createDto.getAccuracy(),
                createDto.getStartTime(),
                createDto.getEndTime()
        );
        return levelSessionRepository.save(session);
    }

    /**
     * Retrieves a level session by its ID.
     * @param id The UUID of the level session.
     * @return An Optional containing the LevelSession if found, or empty if not.
     */
    public Optional<LevelSession> getLevelSessionById(UUID id) {
        return levelSessionRepository.findById(id);
    }

    /**
     * Retrieves all level sessions for a given account.
     * @param accountId The UUID of the account.
     * @return A list of LevelSession entities.
     */
    public List<LevelSession> getLevelSessionsByAccount(UUID accountId) {
        return levelSessionRepository.findByAccountId(accountId);
    }

    /**
     * Retrieves all level sessions for a given level.
     * @param levelId The UUID of the level.
     * @return A list of LevelSession entities.
     */
    public List<LevelSession> getLevelSessionsByLevel(UUID levelId) {
        return levelSessionRepository.findByLevelId(levelId);
    }

    /**
     * Deletes a level session by its ID.
     * @param id The UUID of the level session to delete.
     * @throws EntityNotFoundException if the session with the given ID does not exist.
     */
    @Transactional
    public void deleteLevelSession(UUID id) {
        if (!levelSessionRepository.existsById(id)) {
            throw new EntityNotFoundException("LevelSession not found with ID: " + id);
        }
        levelSessionRepository.deleteById(id);
    }
}
