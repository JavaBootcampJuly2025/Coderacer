package com.coderacer.integration;

import com.coderacer.dto.AccountLoginDTO;
import com.coderacer.dto.LevelSessionCreateDto;
import com.coderacer.dto.LevelSessionDto;
import com.coderacer.enums.Difficulty;
import com.coderacer.enums.ProgrammingLanguage;
import com.coderacer.enums.Role;
import com.coderacer.model.Account;
import com.coderacer.model.Level;
import com.coderacer.model.LevelSession;
import com.coderacer.repository.AccountRepository;
import com.coderacer.repository.EmailVerificationTokenRepository;
import com.coderacer.repository.LevelRepository;
import com.coderacer.repository.LevelSessionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
        // disable real email
        registry.add("spring.mail.host", () -> "localhost");
        registry.add("spring.mail.port", () -> 3025);
    }

    /**
     * Test config that provides a no-op EmailService bean, replacing the real one.
     */
    @TestConfiguration
    static class NoOpEmailConfig {
        @Bean
        @Primary
        public com.coderacer.service.EmailService emailService() {
            return new com.coderacer.service.EmailService() {
                @Override
                public void sendVerificationEmail(com.coderacer.model.Account account, String token) {
                    // nothing :)
                }
            };
        }
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

    @Autowired
    private EmailVerificationTokenRepository tokenRepository;

    private String baseUrl;
    private String accountsBaseUrl;
    private Level testLevel;
    private Account testAccount;
    private String adminToken;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/v1/level-sessions";
        accountsBaseUrl = "http://localhost:" + port + "/api/accounts";

        // Clean up
        levelSessionRepository.deleteAll();
        levelRepository.deleteAll();
        tokenRepository.deleteAll();
        accountRepository.deleteAll();

        // Set up admin for authentication
        setupAdmin();

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
        testAccount.setRole(Role.USER);
        testAccount.setVerified(true);
        testAccount = accountRepository.save(testAccount);
    }

    private void setupAdmin() {
        // Create admin account
        Account admin = new Account();
        admin.setUsername("admin");
        admin.setEmail("admin@example.com");
        admin.setRole(Role.ADMIN);
        admin.setVerified(true);
        admin.setPassword("adminPassword123");
        accountRepository.saveAndFlush(admin);

        // Get JWT token
        AccountLoginDTO login = new AccountLoginDTO("admin", "adminPassword123");
        ResponseEntity<String> resp = restTemplate.postForEntity(
                accountsBaseUrl + "/login", login, String.class);

        if (resp.getStatusCode() == HttpStatus.OK) {
            adminToken = resp.getBody();
        }
    }

    /**
     * Helper to create headers with admin authentication
     */
    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        if (adminToken != null && !adminToken.isEmpty()) {
            headers.setBearerAuth(adminToken);
        }
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
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

        restTemplate.exchange(baseUrl, HttpMethod.POST, new HttpEntity<>(session1, authHeaders()), LevelSession.class);
        restTemplate.exchange(baseUrl, HttpMethod.POST, new HttpEntity<>(session2, authHeaders()), LevelSession.class);

        // When - get raw JSON response as String from GET endpoint with auth
        ResponseEntity<String> rawResponse = restTemplate.exchange(
                baseUrl + "/by-account/" + testAccount.getId(),
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                String.class);

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

        ResponseEntity<LevelSession> createResponse = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                new HttpEntity<>(createDto, authHeaders()),
                LevelSession.class);
        UUID sessionId = createResponse.getBody().getId();

        // When
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                baseUrl + "/" + sessionId,
                HttpMethod.DELETE,
                new HttpEntity<>(authHeaders()),
                Void.class
        );

        // Then
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify level session is deleted with auth
        ResponseEntity<String> getResponse = restTemplate.exchange(
                baseUrl + "/" + sessionId,
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturn404ForNonExistentLevelSession() {
        // When
        UUID nonExistentId = UUID.randomUUID();
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/" + nonExistentId,
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                String.class);

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
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                new HttpEntity<>(createDto, authHeaders()),
                String.class);

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
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                new HttpEntity<>(createDto, authHeaders()),
                String.class);

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
        newAccount.setRole(Role.USER);
        newAccount.setVerified(true);
        newAccount = accountRepository.save(newAccount);

        // When
        ResponseEntity<LevelSession[]> response = restTemplate.exchange(
                baseUrl + "/by-account/" + newAccount.getId(),
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                LevelSession[].class);

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
        ResponseEntity<LevelSession[]> response = restTemplate.exchange(
                baseUrl + "/by-level/" + newLevel.getId(),
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                LevelSession[].class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(0);
    }
}