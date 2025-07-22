package com.coderacer.integration;

import com.coderacer.dto.AccountLoginDTO;
import com.coderacer.dto.LeaderboardEntryDTO;
import com.coderacer.enums.Role;
import com.coderacer.model.Account;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureWebMvc
class LeaderboardEntryApiIT {

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

    private String baseUrl;
    private String accountsBaseUrl;

    @Autowired
    private com.coderacer.repository.AccountRepository accountRepository;

    @Autowired
    private com.coderacer.repository.EmailVerificationTokenRepository tokenRepository;

    private String adminToken;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/leaderboard";
        accountsBaseUrl = "http://localhost:" + port + "/api/accounts";
        tokenRepository.deleteAll();
        accountRepository.deleteAll();

        // Set up admin for authentication if needed
        setupAdmin();
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
    void shouldGetTop10LeaderboardEntries() {
        // Given - Create test accounts with different ratings
        createTestAccount("player1", "player1@example.com", 1000);
        createTestAccount("player2", "player2@example.com", 1500);
        createTestAccount("player3", "player3@example.com", 800);
        createTestAccount("player4", "player4@example.com", 2000);
        createTestAccount("player5", "player5@example.com", 1200);

        // When
        ResponseEntity<List<LeaderboardEntryDTO>> response = restTemplate.exchange(
                baseUrl + "/top",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                new ParameterizedTypeReference<List<LeaderboardEntryDTO>>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Verify ordering (highest rating first)
        List<LeaderboardEntryDTO> entries = response.getBody();
        assertThat(entries.get(0).username()).isEqualTo("player4");
        assertThat(entries.get(0).matchmakingRating()).isEqualTo(2000);
        assertThat(entries.get(1).username()).isEqualTo("player2");
        assertThat(entries.get(1).matchmakingRating()).isEqualTo(1500);
        assertThat(entries.get(2).username()).isEqualTo("player5");
        assertThat(entries.get(2).matchmakingRating()).isEqualTo(1200);
        assertThat(entries.get(3).username()).isEqualTo("player1");
        assertThat(entries.get(3).matchmakingRating()).isEqualTo(1000);
        assertThat(entries.get(4).username()).isEqualTo("player3");
        assertThat(entries.get(4).matchmakingRating()).isEqualTo(800);
    }

    @Test
    void shouldGetTop10WithExactly10Entries() {
        // Given - Create 15 test accounts
        for (int i = 1; i <= 15; i++) {
            createTestAccount("player" + i, "player" + i + "@example.com", i * 100);
        }

        // When
        ResponseEntity<List<LeaderboardEntryDTO>> response = restTemplate.exchange(
                baseUrl + "/top",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                new ParameterizedTypeReference<List<LeaderboardEntryDTO>>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(10);

        // Verify we get the top 10 (highest ratings)
        List<LeaderboardEntryDTO> entries = response.getBody();
        assertThat(entries.get(0).matchmakingRating()).isEqualTo(1500); // player15
        assertThat(entries.get(9).matchmakingRating()).isEqualTo(600);  // player6
    }

    @Test
    void shouldGetEmptyTop10WhenNoAccounts() {
        accountRepository.deleteAll();

        // When
        ResponseEntity<List<LeaderboardEntryDTO>> response = restTemplate.exchange(
                baseUrl + "/top",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                new ParameterizedTypeReference<List<LeaderboardEntryDTO>>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void shouldGetPaginatedLeaderboard() {
        // Given - Create 5 test accounts
        createTestAccount("player1", "player1@example.com", 1000);
        createTestAccount("player2", "player2@example.com", 1500);
        createTestAccount("player3", "player3@example.com", 800);
        createTestAccount("player4", "player4@example.com", 2000);
        createTestAccount("player5", "player5@example.com", 1200);

        // When - Get first page with size 2
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                baseUrl + "?page=0&size=2",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        Map<String, Object> pageResponse = response.getBody();
        assertThat(pageResponse.get("totalPages")).isEqualTo(3);
        assertThat(pageResponse.get("size")).isEqualTo(2);
        assertThat(pageResponse.get("number")).isEqualTo(0);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> content = (List<Map<String, Object>>) pageResponse.get("content");
        assertThat(content).hasSize(2);
        assertThat(content.get(0).get("username")).isEqualTo("player4");
        assertThat(content.get(1).get("username")).isEqualTo("player2");
    }

    @Test
    void shouldGetSecondPageOfPaginatedLeaderboard() {
        // Given - Create 5 test accounts
        createTestAccount("player1", "player1@example.com", 1000);
        createTestAccount("player2", "player2@example.com", 1500);
        createTestAccount("player3", "player3@example.com", 800);
        createTestAccount("player4", "player4@example.com", 2000);
        createTestAccount("player5", "player5@example.com", 1200);

        // When - Get second page with size 2
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                baseUrl + "?page=1&size=2",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        Map<String, Object> pageResponse = response.getBody();
        assertThat(pageResponse.get("number")).isEqualTo(1);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> content = (List<Map<String, Object>>) pageResponse.get("content");
        assertThat(content).hasSize(2);
        assertThat(content.get(0).get("username")).isEqualTo("player5");
        assertThat(content.get(1).get("username")).isEqualTo("player1");
    }

    @Test
    void shouldGetLeaderboardEntryByUsername() {
        // Given
        createTestAccount("targetplayer", "target@example.com", 1337);
        createTestAccount("otherplayer", "other@example.com", 500);

        // When
        ResponseEntity<LeaderboardEntryDTO> response = restTemplate.exchange(
                baseUrl + "/targetplayer",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                LeaderboardEntryDTO.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().username()).isEqualTo("targetplayer");
        assertThat(response.getBody().matchmakingRating()).isEqualTo(1337);
        assertThat(response.getBody().id()).isNotNull();
    }

    @Test
    void shouldReturn404ForNonExistentUsername() {
        // Given
        createTestAccount("existingplayer", "existing@example.com", 1000);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/nonexistent",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldHandleUsernameWithSpecialCharacters() {
        // Given
        createTestAccount("player_123", "player123@example.com", 999);

        // When
        ResponseEntity<LeaderboardEntryDTO> response = restTemplate.exchange(
                baseUrl + "/player_123",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                LeaderboardEntryDTO.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().username()).isEqualTo("player_123");
        assertThat(response.getBody().matchmakingRating()).isEqualTo(999);
    }

    @Test
    void shouldHandleZeroRatingCorrectly() {
        // Given
        createTestAccount("newplayer", "new@example.com", 0);

        // When
        ResponseEntity<LeaderboardEntryDTO> response = restTemplate.exchange(
                baseUrl + "/newplayer",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                LeaderboardEntryDTO.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().username()).isEqualTo("newplayer");
        assertThat(response.getBody().matchmakingRating()).isEqualTo(0);
    }

    @Test
    void shouldReturnEmptyPageWhenRequestingBeyondLastPage() {
        // Given - Create 2 accounts
        createTestAccount("player1", "player1@example.com", 1000);
        createTestAccount("player2", "player2@example.com", 1500);

        // When - Request page 10 with size 10
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                baseUrl + "?page=10&size=10",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        Map<String, Object> pageResponse = response.getBody();
        assertThat(pageResponse.get("totalPages")).isEqualTo(1);
        assertThat(pageResponse.get("number")).isEqualTo(10);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> content = (List<Map<String, Object>>) pageResponse.get("content");
        assertThat(content).isEmpty();
    }

    @Test
    void shouldMaintainSortingConsistencyAcrossPages() {
        // Given - Create accounts with some duplicate ratings
        createTestAccount("player1", "player1@example.com", 1000);
        createTestAccount("player2", "player2@example.com", 1000);
        createTestAccount("player3", "player3@example.com", 1500);
        createTestAccount("player4", "player4@example.com", 2000);

        // When - Get both pages
        ResponseEntity<Map<String, Object>> page1Response = restTemplate.exchange(
                baseUrl + "?page=0&size=2",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        ResponseEntity<Map<String, Object>> page2Response = restTemplate.exchange(
                baseUrl + "?page=1&size=2",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        // Then
        assertThat(page1Response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(page2Response.getStatusCode()).isEqualTo(HttpStatus.OK);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> page1Content = (List<Map<String, Object>>) page1Response.getBody().get("content");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> page2Content = (List<Map<String, Object>>) page2Response.getBody().get("content");

        // Verify that all entries are accounted for and properly sorted
        assertThat(page1Content).hasSize(2);
        assertThat(page2Content).hasSize(2);

        // First page should have highest ratings
        assertThat(page1Content.get(0).get("matchmakingRating")).isEqualTo(2000);
        assertThat(page1Content.get(1).get("matchmakingRating")).isEqualTo(1500);

        // Second page should have lower ratings
        assertThat(page2Content.get(0).get("matchmakingRating")).isEqualTo(1000);
        assertThat(page2Content.get(1).get("matchmakingRating")).isEqualTo(1000);
    }

    @Test
    void shouldHandleInvalidPageParameters() {
        // Given - Create one account
        createTestAccount("player1", "player1@example.com", 1000);

        // When - Request with negative page
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "?page=-1&size=5",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                String.class
        );

        // Then - Should handle gracefully (most Spring implementations convert negative to 0)
        assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldHandleInvalidSizeParameters() {
        // Given - Create one account
        createTestAccount("player1", "player1@example.com", 1000);

        // When - Request with zero size
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "?page=0&size=0",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                String.class
        );

        // Then - Should handle gracefully
        assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.BAD_REQUEST);
    }

    private void createTestAccount(String username, String email, int rating) {
        Account account = new Account();
        account.setUsername(username);
        account.setEmail(email);
        account.setPassword("password123");
        account.setRating(rating);
        account.setVerified(false);
        account.setRole(Role.USER); // Set default role
        accountRepository.saveAndFlush(account);
    }
}