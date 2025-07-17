package com.coderacer.integration;

import com.coderacer.dto.LevelSessionCreateDto;
import com.coderacer.dto.LevelSessionDto;
import com.coderacer.enums.Difficulty;
import com.coderacer.enums.ProgrammingLanguage;
import com.coderacer.model.Account;
import com.coderacer.model.Level;
import com.coderacer.model.LevelSession;
import com.coderacer.repository.AccountRepository;
import com.coderacer.repository.LevelRepository;
import com.coderacer.repository.LevelSessionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureWebMvc
class LevelSessionApiIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LevelSessionRepository levelSessionRepository;

    @Autowired
    private LevelRepository levelRepository;

    @Autowired
    private AccountRepository accountRepository;

    private String baseUrl;
    private Level testLevel;
    private Account testAccount;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/v1/level-sessions";

        // Clean up
        levelSessionRepository.deleteAll();
        levelRepository.deleteAll();
        accountRepository.deleteAll();

        // Create test data
        testLevel = new Level();
        testLevel.setCodeSnippet("public class Test { public static void main(String[] args) { System.out.println(\"Test\"); int x = 5; int y = 10; int sum = x + y; System.out.println(\"Sum: \" + sum); } }");
        testLevel.setLanguage(ProgrammingLanguage.JAVA);
        testLevel.setDifficulty(Difficulty.EASY);
        testLevel.setTags(Arrays.asList("test"));
        testLevel = levelRepository.save(testLevel);

        testAccount = new Account();
        testAccount.setUsername("testuser");
        testAccount.setEmail("test@example.com");
        testAccount.setPassword("password123");
        testAccount.setRating(1000);
        testAccount = accountRepository.save(testAccount);
    }

    @Test
    void shouldGetLevelSessionsByAccount() throws Exception {
        // Given - Create two level sessions for the same account
        LevelSessionCreateDto session1 = new LevelSessionCreateDto(
                testLevel.getId(),
                testAccount.getId(),
                80.0,
                90.0,
                LocalDateTime.now().minusMinutes(10),
                LocalDateTime.now().minusMinutes(5)
        );

        LevelSessionCreateDto session2 = new LevelSessionCreateDto(
                testLevel.getId(),
                testAccount.getId(),
                85.0,
                95.0,
                LocalDateTime.now().minusMinutes(5),
                LocalDateTime.now()
        );

        restTemplate.postForEntity(baseUrl, session1, LevelSession.class);
        restTemplate.postForEntity(baseUrl, session2, LevelSession.class);

        // When - get raw JSON response as String from GET endpoint
        ResponseEntity<String> rawResponse = restTemplate.getForEntity(
                baseUrl + "/by-account/" + testAccount.getId(), String.class);

        assertThat(rawResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Then - manually deserialize with ObjectMapper into DTO array (no timestamps here)
        ObjectMapper mapper = new ObjectMapper();
        LevelSessionDto[] dtos = mapper.readValue(rawResponse.getBody(), LevelSessionDto[].class);

        // Assert size and values
        assertThat(dtos).hasSize(2);
        assertThat(Arrays.stream(dtos))
                .extracting(LevelSessionDto::getCpm)
                .containsExactlyInAnyOrder(80.0, 85.0);
    }
    @Test
    void shouldGetLevelSessionsByLevel() throws Exception {
        // Given - Create another account and level sessions
        Account anotherAccount = new Account();
        anotherAccount.setUsername("anotheruser");
        anotherAccount.setEmail("another@example.com");
        anotherAccount.setPassword("password456");
        anotherAccount.setRating(1200);
        anotherAccount = accountRepository.save(anotherAccount);

        LevelSessionCreateDto session1 = new LevelSessionCreateDto(
                testLevel.getId(),
                testAccount.getId(),
                75.0,
                88.0,
                LocalDateTime.now().minusMinutes(10),
                LocalDateTime.now().minusMinutes(5)
        );

        LevelSessionCreateDto session2 = new LevelSessionCreateDto(
                testLevel.getId(),
                anotherAccount.getId(),
                90.0,
                94.0,
                LocalDateTime.now().minusMinutes(5),
                LocalDateTime.now()
        );

        restTemplate.postForEntity(baseUrl, session1, LevelSession.class);
        restTemplate.postForEntity(baseUrl, session2, LevelSession.class);

        // When - get raw JSON response as String from GET endpoint
        ResponseEntity<String> rawResponse = restTemplate.getForEntity(
                baseUrl + "/by-level/" + testLevel.getId(), String.class);

        // Then - assert HTTP status is OK
        assertThat(rawResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Deserialize to DTO array
        ObjectMapper mapper = new ObjectMapper();
        LevelSessionDto[] dtos = mapper.readValue(rawResponse.getBody(), LevelSessionDto[].class);

        // Assert size and CPM values for created sessions
        assertThat(dtos).hasSize(2);
        assertThat(Arrays.stream(dtos))
                .extracting(LevelSessionDto::getCpm)
                .containsExactlyInAnyOrder(75.0, 90.0);
    }

    @Test
    void shouldDeleteLevelSession() {
        // Given
        LevelSessionCreateDto createDto = new LevelSessionCreateDto(
                testLevel.getId(),
                testAccount.getId(),
                80.0,
                90.0,
                LocalDateTime.now().minusMinutes(10),
                LocalDateTime.now().minusMinutes(5)
        );

        ResponseEntity<LevelSession> createResponse = restTemplate.postForEntity(
                baseUrl, createDto, LevelSession.class);
        UUID sessionId = createResponse.getBody().getId();

        // When
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                baseUrl + "/" + sessionId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        // Then
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify level session is deleted
        ResponseEntity<String> getResponse = restTemplate.getForEntity(
                baseUrl + "/" + sessionId, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturn404ForNonExistentLevelSession() {
        // When
        UUID nonExistentId = UUID.randomUUID();
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/" + nonExistentId, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturn400WhenCreatingWithNonExistentLevel() {
        // Given
        LevelSessionCreateDto createDto = new LevelSessionCreateDto(
                UUID.randomUUID(), // Non-existent level ID
                testAccount.getId(),
                80.0,
                90.0,
                LocalDateTime.now().minusMinutes(10),
                LocalDateTime.now().minusMinutes(5)
        );

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl, createDto, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturn400WhenCreatingWithNonExistentAccount() {
        // Given
        LevelSessionCreateDto createDto = new LevelSessionCreateDto(
                testLevel.getId(),
                UUID.randomUUID(), // Non-existent account ID
                80.0,
                90.0,
                LocalDateTime.now().minusMinutes(10),
                LocalDateTime.now().minusMinutes(5)
        );

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl, createDto, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturnEmptyListForAccountWithNoSessions() {
        // Given
        Account newAccount = new Account();
        newAccount.setUsername("newuser");
        newAccount.setEmail("new@example.com");
        newAccount.setPassword("password789");
        newAccount.setRating(800);
        newAccount = accountRepository.save(newAccount);

        // When
        ResponseEntity<LevelSession[]> response = restTemplate.getForEntity(
                baseUrl + "/by-account/" + newAccount.getId(), LevelSession[].class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(0);
    }

    @Test
    void shouldReturnEmptyListForLevelWithNoSessions() {
        // Given
        Level newLevel = new Level();
        newLevel.setCodeSnippet("public class NewTest { public static void main(String[] args) { System.out.println(\"New Test\"); for(int i = 0; i < 10; i++) { System.out.println(i); } } }");
        newLevel.setLanguage(ProgrammingLanguage.JAVA);
        newLevel.setDifficulty(Difficulty.MEDIUM);
        newLevel.setTags(Arrays.asList("new"));
        newLevel = levelRepository.save(newLevel);

        // When
        ResponseEntity<LevelSession[]> response = restTemplate.getForEntity(
                baseUrl + "/by-level/" + newLevel.getId(), LevelSession[].class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(0);
    }
}