package com.coderacer.controller;

import com.coderacer.dto.LevelSessionDto;
import com.coderacer.model.LevelSession;
import com.coderacer.service.AccountService;
import com.coderacer.service.LevelSessionService;
import com.coderacer.dto.LevelSessionCreateDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/level-sessions")
public class LevelSessionController {

    private final LevelSessionService levelSessionService;
    private final AccountService accountService;

    @Autowired
    public LevelSessionController(LevelSessionService levelSessionService, AccountService accountService) {
        this.levelSessionService = levelSessionService;
        this.accountService = accountService;
    }

    /**
     * Creates a new LevelSession using data from a DTO.
     * Also, since this implies a finished game, the method updates player matchmaking rating as well.
     *
     * @param createDto The DTO containing the data for the new session, sent in the request body.
     * @return ResponseEntity with the created LevelSession and HTTP status 201 (Created).
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PostMapping
    public ResponseEntity<LevelSession> createLevelSession(@RequestBody LevelSessionCreateDto createDto) {
        try {
            LevelSession newSession = levelSessionService.createLevelSession(createDto);
            accountService.updateRating(createDto);
            return new ResponseEntity<>(newSession, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            // Return a 400 Bad Request if a referenced Level or Account is not found.
            // In a production application, you might return a custom error response DTO
            // with more details about what went wrong.
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Retrieves a LevelSession by its ID.
     *
     * @param id The UUID of the LevelSession.
     * @return ResponseEntity with the LevelSession if found, or HTTP status 404 (Not Found).
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<LevelSession> getLevelSessionById(@PathVariable UUID id) {
        return levelSessionService.getLevelSessionById(id)
                .map(session -> new ResponseEntity<>(session, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Retrieves all LevelSessions for a specific account.
     *
     * @param accountId The UUID of the account.
     * @return ResponseEntity with a list of LevelSessions and HTTP status 200 (OK).
     */
    @PreAuthorize("hasRole('ADMIN') or #id == principal")
    @GetMapping("/by-account/{accountId}")
    public ResponseEntity<List<LevelSessionDto>> getLevelSessionsByAccount(@PathVariable UUID accountId) {
        List<LevelSession> sessions = levelSessionService.getLevelSessionsByAccount(accountId);
        List<LevelSessionDto> dtos = sessions.stream()
                .map(LevelSessionDto::fromEntity)
                .toList();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    /**
     * Retrieves all LevelSessions for a specific level.
     *
     * @param levelId The UUID of the level.
     * @return ResponseEntity with a list of LevelSessions and HTTP status 200 (OK).
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/by-level/{levelId}")
    public ResponseEntity<List<LevelSession>> getLevelSessionsByLevel(@PathVariable UUID levelId) {
        List<LevelSession> sessions = levelSessionService.getLevelSessionsByLevel(levelId);
        return new ResponseEntity<>(sessions, HttpStatus.OK);
    }
    /**
     * Deletes a LevelSession by its ID.
     *
     * @param id The UUID of the LevelSession to delete.
     * @return ResponseEntity with HTTP status 204 (No Content) on successful deletion,
     * or HTTP status 404 (Not Found) if the session does not exist.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLevelSession(@PathVariable UUID id) {
        try {
            levelSessionService.deleteLevelSession(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
