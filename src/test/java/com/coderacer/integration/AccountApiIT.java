package com.coderacer.integration;

import com.coderacer.dto.*;
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
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureWebMvc
class AccountApiIT {

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

    @Autowired
    private com.coderacer.repository.EmailVerificationTokenRepository tokenRepository;

    @Autowired
    private com.coderacer.repository.AccountRepository accountRepository;

    private String adminToken;

    @BeforeEach
    void setUpAdmin() {
        tokenRepository.deleteAll();
        accountRepository.deleteAll();
        baseUrl = "http://localhost:" + port + "/api/accounts";

        // Create admin acc
        Account admin = new Account();
        admin.setUsername("admin");
        admin.setEmail("admin@example.com");
        admin.setRole(Role.ADMIN);
        admin.setVerified(true);
        admin.setPassword("idkWhatAmIDoingWithMyLife");
        accountRepository.saveAndFlush(admin);

        // Get JWT token
        AccountLoginDTO login = new AccountLoginDTO("admin", "idkWhatAmIDoingWithMyLife");
        ResponseEntity<String> resp = restTemplate.postForEntity(
                baseUrl + "/login", login, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        adminToken = resp.getBody();
    }

    /**
     * Helper to login and return HttpHeaders with Bearer token
     */
    private HttpHeaders adminHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Test
    void shouldCreateAndRetrieveAccount() {
        // Given
        AccountCreateDTO newAccount = new AccountCreateDTO(
                "testuser",
                "test@example.com",
                "password123"
        );

        // When - Create account
        ResponseEntity<AccountDTO> createResponse = restTemplate.postForEntity(
                baseUrl, newAccount, AccountDTO.class);

        // Then - Verify creation
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().id()).isNotNull();
        assertThat(createResponse.getBody().username()).isEqualTo(newAccount.username());
        assertThat(createResponse.getBody().email()).isEqualTo(newAccount.email());
        assertThat(createResponse.getBody().rating()).isEqualTo(0);
        assertThat(createResponse.getBody().verified()).isFalse();

        // When - Retrieve account with admin headers
        UUID accountId = createResponse.getBody().id();
        ResponseEntity<AccountDTO> getResponse = restTemplate.exchange(
                baseUrl + "/" + accountId,
                HttpMethod.GET,
                new HttpEntity<>(adminHeaders()),
                AccountDTO.class);

        // Then - Verify retrieval
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isEqualTo(createResponse.getBody());
    }

    @Test
    void shouldGetAccountByUsername() {
        // Given
        AccountCreateDTO account = new AccountCreateDTO(
                "findme",
                "findme@example.com",
                "password123"
        );

        restTemplate.postForEntity(baseUrl, account, AccountDTO.class);

        // When - with admin headers
        ResponseEntity<AccountDTO> response = restTemplate.exchange(
                baseUrl + "/username/findme",
                HttpMethod.GET,
                new HttpEntity<>(adminHeaders()),
                AccountDTO.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().username()).isEqualTo("findme");
    }

    @Test
    void shouldUpdateAccount() {
        // Given - Create account
        AccountCreateDTO originalAccount = new AccountCreateDTO(
                "original",
                "original@example.com",
                "password123"
        );

        ResponseEntity<AccountDTO> createResponse = restTemplate.postForEntity(
                baseUrl, originalAccount, AccountDTO.class);
        UUID accountId = createResponse.getBody().id();

        // When - Update account with admin headers
        AccountUpdateDTO updateDTO = new AccountUpdateDTO(
                "updated@example.com",
                100,
                true
        );

        ResponseEntity<AccountDTO> updateResponse = restTemplate.exchange(
                baseUrl + "/" + accountId,
                HttpMethod.PUT,
                new HttpEntity<>(updateDTO, adminHeaders()),
                AccountDTO.class
        );

        // Then
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody().id()).isEqualTo(accountId);
        assertThat(updateResponse.getBody().email()).isEqualTo("updated@example.com");
        assertThat(updateResponse.getBody().rating()).isEqualTo(100);
        assertThat(updateResponse.getBody().verified()).isTrue();
    }

    @Test
    void shouldDeleteAccount() {
        // Given
        AccountCreateDTO account = new AccountCreateDTO(
                "todelete",
                "delete@example.com",
                "password123"
        );

        ResponseEntity<AccountDTO> createResponse = restTemplate.postForEntity(
                baseUrl, account, AccountDTO.class);
        UUID accountId = createResponse.getBody().id();

        // When - Delete with admin headers
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                baseUrl + "/" + accountId,
                HttpMethod.DELETE,
                new HttpEntity<>(adminHeaders()),
                Void.class
        );

        // Then
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify account is deleted - also use admin headers for verification
        ResponseEntity<String> getResponse = restTemplate.exchange(
                baseUrl + "/" + accountId,
                HttpMethod.GET,
                new HttpEntity<>(adminHeaders()),
                String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldChangePassword() {
        // Given
        AccountCreateDTO account = new AccountCreateDTO(
                "pwdchange",
                "pwd@example.com",
                "oldpassword"
        );

        ResponseEntity<AccountDTO> createResponse = restTemplate.postForEntity(
                baseUrl, account, AccountDTO.class);
        UUID accountId = createResponse.getBody().id();

        // When - Change password with admin headers
        PasswordChangeDTO passwordChange = new PasswordChangeDTO(
                "oldpassword",
                "newpassword123"
        );

        ResponseEntity<Void> changeResponse = restTemplate.exchange(
                baseUrl + "/" + accountId + "/password",
                HttpMethod.PUT,
                new HttpEntity<>(passwordChange, adminHeaders()),
                Void.class
        );

        // Then
        assertThat(changeResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void shouldReturn404ForNonExistentAccount() {
        // When - Use admin headers for consistency
        UUID nonExistentId = UUID.randomUUID();
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/" + nonExistentId,
                HttpMethod.GET,
                new HttpEntity<>(adminHeaders()),
                String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturn409ForDuplicateUsername() {
        // Given - Create first account
        AccountCreateDTO account1 = new AccountCreateDTO(
                "duplicate",
                "first@example.com",
                "password123"
        );
        restTemplate.postForEntity(baseUrl, account1, AccountDTO.class);

        // When - Try to create second account with same username
        AccountCreateDTO account2 = new AccountCreateDTO(
                "duplicate",
                "second@example.com",
                "password123"
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl, account2, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void shouldReturn400ForInvalidData() {
        // Given - Invalid account (short username)
        AccountCreateDTO invalidAccount = new AccountCreateDTO(
                "ab", // Less than 3 characters
                "test@example.com",
                "password123"
        );

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl, invalidAccount, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}