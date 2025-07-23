package com.coderacer.integration;

import com.coderacer.dto.AccountLoginDTO;
import com.coderacer.dto.LevelDTO;
import com.coderacer.dto.LevelModifyDTO;
import com.coderacer.enums.Difficulty;
import com.coderacer.enums.ProgrammingLanguage;
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

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureWebMvc
class LevelApiIT {

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
    private com.coderacer.repository.LevelRepository levelRepository;

    @Autowired
    private com.coderacer.repository.AccountRepository accountRepository;

    @Autowired
    private com.coderacer.repository.EmailVerificationTokenRepository tokenRepository;

    private String adminToken;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/levels";
        accountsBaseUrl = "http://localhost:" + port + "/api/accounts";
        levelRepository.deleteAll();
        tokenRepository.deleteAll();
        accountRepository.deleteAll();

        // Set up admin for authentication
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
    void shouldCreateAndRetrieveLevel() {
        // Given
        LevelModifyDTO newLevel = new LevelModifyDTO(
                "public class HelloWorld { public static void main(String[] args) { System.out.println(\"Hello, World!\"); } }",
                ProgrammingLanguage.JAVA,
                Difficulty.EASY,
                Arrays.asList("beginner", "hello")
        );

        // When - Create level with auth
        ResponseEntity<LevelDTO> createResponse = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                new HttpEntity<>(newLevel, authHeaders()),
                LevelDTO.class
        );

        // Then - Verify creation
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().id()).isNotNull();
        assertThat(createResponse.getBody().codeSnippet()).isEqualTo(newLevel.codeSnippet());
        assertThat(createResponse.getBody().language()).isEqualTo(newLevel.language());
        assertThat(createResponse.getBody().difficulty()).isEqualTo(newLevel.difficulty());
        assertThat(createResponse.getBody().tags()).containsExactlyInAnyOrder("beginner", "hello");

        // When - Retrieve level with auth
        UUID levelId = createResponse.getBody().id();
        ResponseEntity<LevelDTO> getResponse = restTemplate.exchange(
                baseUrl + "/" + levelId,
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                LevelDTO.class
        );

        // Then - Verify retrieval
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isEqualTo(createResponse.getBody());
    }

    @Test
    void shouldGetAllLevels() {
        // Given - Create two levels
        LevelModifyDTO level1 = new LevelModifyDTO(
                "public class Test1 { public static void main(String[] args) { System.out.println(\"Test1 and some other stuff\"); } }",
                ProgrammingLanguage.JAVA,
                Difficulty.EASY,
                Arrays.asList("test")
        );
        LevelModifyDTO level2 = new LevelModifyDTO(
                "public class Test2 { public static void main(String[] args) { System.out.println(\"Test2 and some other stuff\"); } }",
                ProgrammingLanguage.JAVA,
                Difficulty.MEDIUM,
                Arrays.asList("test")
        );

        restTemplate.exchange(baseUrl, HttpMethod.POST, new HttpEntity<>(level1, authHeaders()), LevelDTO.class);
        restTemplate.exchange(baseUrl, HttpMethod.POST, new HttpEntity<>(level2, authHeaders()), LevelDTO.class);

        // When
        ResponseEntity<LevelDTO[]> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                LevelDTO[].class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(Arrays.stream(response.getBody()))
                .extracting(LevelDTO::language)
                .containsOnly(ProgrammingLanguage.JAVA);
    }

    @Test
    void shouldGetLevelsByLanguage() {
        // Given
        LevelModifyDTO javaLevel = new LevelModifyDTO(
                "print('Hello Python or Java idk') # This is a simple Python program that prints a greeting message to the console}",
                ProgrammingLanguage.JAVA,
                Difficulty.EASY,
                Arrays.asList("java")
        );
        LevelModifyDTO pythonLevel = new LevelModifyDTO(
                "print('Hello Python') # This is a simple Python program that prints a greeting message to the console",
                ProgrammingLanguage.PYTHON,
                Difficulty.EASY,
                Arrays.asList("python")
        );

        restTemplate.exchange(baseUrl, HttpMethod.POST, new HttpEntity<>(javaLevel, authHeaders()), LevelDTO.class);
        restTemplate.exchange(baseUrl, HttpMethod.POST, new HttpEntity<>(pythonLevel, authHeaders()), LevelDTO.class);

        // When
        ResponseEntity<LevelDTO[]> response = restTemplate.exchange(
                baseUrl + "/language/JAVA",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                LevelDTO[].class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody()[0].language()).isEqualTo(ProgrammingLanguage.JAVA);
    }

    @Test
    void shouldGetLevelsByDifficulty() {
        // Given
        LevelModifyDTO easyLevel = new LevelModifyDTO(
                "public class HelloWorld { public static void main(String[] args) { System.out.println(\"Hello, World!\"); } }",
                ProgrammingLanguage.JAVA,
                Difficulty.EASY,
                Arrays.asList("easy")
        );
        LevelModifyDTO hardLevel = new LevelModifyDTO(
                "public class HelloWorld { public static void main(String[] args) { System.out.println(\"Hello, World!\"); } }",
                ProgrammingLanguage.JAVA,
                Difficulty.HARD,
                Arrays.asList("hard")
        );

        restTemplate.exchange(baseUrl, HttpMethod.POST, new HttpEntity<>(easyLevel, authHeaders()), LevelDTO.class);
        restTemplate.exchange(baseUrl, HttpMethod.POST, new HttpEntity<>(hardLevel, authHeaders()), LevelDTO.class);

        // When
        ResponseEntity<LevelDTO[]> response = restTemplate.exchange(
                baseUrl + "/difficulty/EASY",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                LevelDTO[].class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody()[0].difficulty()).isEqualTo(Difficulty.EASY);
    }

    @Test
    void shouldGetRandomLevel() {
        // Given
        LevelModifyDTO level1 = new LevelModifyDTO(
                "public class HelloWorld { public static void main(String[] args) { System.out.println(\"Hello, World!\"); } }",
                ProgrammingLanguage.JAVA,
                Difficulty.EASY,
                Arrays.asList("random")
        );
        LevelModifyDTO level2 = new LevelModifyDTO(
                "public class HelloWorld { public static void main(String[] args) { System.out.println(\"Hello, World!\"); } }",
                ProgrammingLanguage.JAVA,
                Difficulty.EASY,
                Arrays.asList("random")
        );

        restTemplate.exchange(baseUrl, HttpMethod.POST, new HttpEntity<>(level1, authHeaders()), LevelDTO.class);
        restTemplate.exchange(baseUrl, HttpMethod.POST, new HttpEntity<>(level2, authHeaders()), LevelDTO.class);

        // When
        ResponseEntity<LevelDTO> response = restTemplate.exchange(
                baseUrl + "/random?language=JAVA&difficulty=EASY",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                LevelDTO.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().language()).isEqualTo(ProgrammingLanguage.JAVA);
        assertThat(response.getBody().difficulty()).isEqualTo(Difficulty.EASY);
    }

    @Test
    void shouldUpdateLevel() {
        // Given - Create a level
        LevelModifyDTO originalLevel = new LevelModifyDTO(
                "public class Original { public static void main(String[] args) { System.out.println(\"Original\"); } }",
                ProgrammingLanguage.JAVA,
                Difficulty.EASY,
                Arrays.asList("original")
        );

        ResponseEntity<LevelDTO> createResponse = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                new HttpEntity<>(originalLevel, authHeaders()),
                LevelDTO.class
        );
        UUID levelId = createResponse.getBody().id();

        // When - Update the level
        LevelModifyDTO updatedLevel = new LevelModifyDTO(
                "public class Updated { public static void main(String[] args) { System.out.println(\"Updated I think\"); } }",
                ProgrammingLanguage.JAVA,
                Difficulty.MEDIUM,
                Arrays.asList("updated")
        );

        ResponseEntity<LevelDTO> updateResponse = restTemplate.exchange(
                baseUrl + "/" + levelId,
                HttpMethod.PUT,
                new HttpEntity<>(updatedLevel, authHeaders()),
                LevelDTO.class
        );

        // Then
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody().id()).isEqualTo(levelId);
        assertThat(updateResponse.getBody().codeSnippet()).isEqualTo(updatedLevel.codeSnippet());
        assertThat(updateResponse.getBody().difficulty()).isEqualTo(Difficulty.MEDIUM);
        assertThat(updateResponse.getBody().tags()).containsExactly("updated");
    }

    @Test
    void shouldDeleteLevel() {
        // Given
        LevelModifyDTO level = new LevelModifyDTO(
                "public class HelloWorld { public static void main(String[] args) { System.out.println(\"Hello, World!\"); } }",
                ProgrammingLanguage.JAVA,
                Difficulty.EASY,
                Arrays.asList("delete")
        );

        ResponseEntity<LevelDTO> createResponse = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                new HttpEntity<>(level, authHeaders()),
                LevelDTO.class
        );
        UUID levelId = createResponse.getBody().id();

        // When
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                baseUrl + "/" + levelId,
                HttpMethod.DELETE,
                new HttpEntity<>(authHeaders()),
                Void.class
        );

        // Then
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify level is deleted
        ResponseEntity<String> getResponse = restTemplate.exchange(
                baseUrl + "/" + levelId,
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                String.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturn404ForNonExistentLevel() {
        // When
        UUID nonExistentId = UUID.randomUUID();
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/" + nonExistentId,
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturn404ForRandomLevelWithNoMatches() {
        // When - Try to get random level with no data
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/random?language=JAVA&difficulty=EASY",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldValidateRequiredFields() {
        // Given - Invalid level (code snippet too short)
        LevelModifyDTO invalidLevel = new LevelModifyDTO(
                "short", // Less than 100 characters
                ProgrammingLanguage.JAVA,
                Difficulty.EASY,
                Arrays.asList("invalid")
        );

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                new HttpEntity<>(invalidLevel, authHeaders()),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}