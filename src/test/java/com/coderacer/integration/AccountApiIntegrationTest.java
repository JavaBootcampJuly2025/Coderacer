package com.coderacer.integration;

import com.coderacer.dto.*;
import com.coderacer.model.Account;
import com.coderacer.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureWebMvc
class AccountApiIntegrationTest {

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

    private String baseUrl;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/accounts";
        accountRepository.deleteAll();
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
        assertThat(createResponse.getBody().verified()).isFalse();

        // When - Retrieve account by ID
        UUID accountId = createResponse.getBody().id();
        ResponseEntity<AccountDTO> getResponse = restTemplate.getForEntity(
                baseUrl + "/" + accountId, AccountDTO.class);

        // Then - Verify retrieval by ID
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isEqualTo(createResponse.getBody());

        // When - Retrieve account by username
        ResponseEntity<AccountDTO> getByUsernameResponse = restTemplate.getForEntity(
                baseUrl + "/username/" + newAccount.username(), AccountDTO.class);

        // Then - Verify retrieval by username
        assertThat(getByUsernameResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getByUsernameResponse.getBody()).isEqualTo(createResponse.getBody());
    }

    @Test
    void shouldGetAllAccounts() {
        // Given - Create two accounts
        AccountCreateDTO account1 = new AccountCreateDTO(
                "user1",
                "user1@example.com",
                "password123"
        );
        AccountCreateDTO account2 = new AccountCreateDTO(
                "user2",
                "user2@example.com",
                "password123"
        );

        restTemplate.postForEntity(baseUrl, account1, AccountDTO.class);
        restTemplate.postForEntity(baseUrl, account2, AccountDTO.class);

        // When
        ResponseEntity<AccountDTO[]> response = restTemplate.getForEntity(baseUrl, AccountDTO[].class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(Arrays.stream(response.getBody()))
                .extracting(AccountDTO::username)
                .containsOnly("user1", "user2");
    }

    @Test
    void shouldUpdateAccount() {
        // Given - Create an account
        AccountCreateDTO originalAccount = new AccountCreateDTO(
                "updateuser",
                "original@example.com",
                "password123"
        );

        ResponseEntity<AccountDTO> createResponse = restTemplate.postForEntity(
                baseUrl, originalAccount, AccountDTO.class);
        UUID accountId = createResponse.getBody().id();

        // When - Update the account
        AccountUpdateDTO updatedAccount = new AccountUpdateDTO(
                "updated@example.com",
                100,
                true
        );

        ResponseEntity<AccountDTO> updateResponse = restTemplate.exchange(
                baseUrl + "/" + accountId,
                HttpMethod.PUT,
                new HttpEntity<>(updatedAccount),
                AccountDTO.class
        );

        // Then
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody().id()).isEqualTo(accountId);
        assertThat(updateResponse.getBody().email()).isEqualTo(updatedAccount.email());
        assertThat(updateResponse.getBody().rating()).isEqualTo(updatedAccount.rating());
        assertThat(updateResponse.getBody().verified()).isEqualTo(updatedAccount.verified());
    }

    @Test
    void shouldDeleteAccount() {
        // Given
        AccountCreateDTO accountToDelete = new AccountCreateDTO(
                "deleteuser",
                "delete@example.com",
                "password123"
        );

        ResponseEntity<AccountDTO> createResponse = restTemplate.postForEntity(
                baseUrl, accountToDelete, AccountDTO.class);
        UUID accountId = createResponse.getBody().id();

        // When
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                baseUrl + "/" + accountId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        // Then
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify account is deleted
        ResponseEntity<String> getResponse = restTemplate.getForEntity(
                baseUrl + "/" + accountId, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldChangePassword() {
        // Given
        AccountCreateDTO account = new AccountCreateDTO(
                "passworduser",
                "password@example.com",
                "oldpassword"
        );

        ResponseEntity<AccountDTO> createResponse = restTemplate.postForEntity(
                baseUrl, account, AccountDTO.class);
        UUID accountId = createResponse.getBody().id();

        // When
        PasswordChangeDTO passwordChange = new PasswordChangeDTO(
                "oldpassword",
                "newpassword"
        );

        ResponseEntity<Void> changePasswordResponse = restTemplate.exchange(
                baseUrl + "/" + accountId + "/password",
                HttpMethod.PUT,
                new HttpEntity<>(passwordChange),
                Void.class
        );

        // Then
        assertThat(changePasswordResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void shouldReturn404ForNonExistentAccount() {
        // When
        UUID nonExistentId = UUID.randomUUID();
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/" + nonExistentId, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturn404ForNonExistentUsername() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/username/nonexistent", String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturn400ForInvalidAccountCreation() {
        // Given - Invalid account (username too short)
        AccountCreateDTO invalidAccount = new AccountCreateDTO(
                "ab",
                "invalid@example.com",
                "password123"
        );

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl, invalidAccount, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturn400ForInvalidAccountUpdate() {
        // Given - Create an account
        AccountCreateDTO originalAccount = new AccountCreateDTO(
                "invalidupdateuser",
                "originalinvalid@example.com",
                "password123"
        );

        ResponseEntity<AccountDTO> createResponse = restTemplate.postForEntity(
                baseUrl, originalAccount, AccountDTO.class);
        UUID accountId = createResponse.getBody().id();

        // When - Update with invalid email
        AccountUpdateDTO invalidUpdate = new AccountUpdateDTO(
                "invalid-email",
                10,
                false
        );

        ResponseEntity<String> updateResponse = restTemplate.exchange(
                baseUrl + "/" + accountId,
                HttpMethod.PUT,
                new HttpEntity<>(invalidUpdate),
                String.class
        );

        // Then
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturn400ForInvalidPasswordChange() {
        // Given
        AccountCreateDTO account = new AccountCreateDTO(
                "invalidpassuser",
                "invalidpass@example.com",
                "oldpassword"
        );

        ResponseEntity<AccountDTO> createResponse = restTemplate.postForEntity(
                baseUrl, account, AccountDTO.class);
        UUID accountId = createResponse.getBody().id();

        // When - Mismatched new passwords
        PasswordChangeDTO invalidPasswordChange = new PasswordChangeDTO(
                "oldpassword",
                "mismatchednewpassword"
        );

        ResponseEntity<String> changePasswordResponse = restTemplate.exchange(
                baseUrl + "/" + accountId + "/password",
                HttpMethod.PUT,
                new HttpEntity<>(invalidPasswordChange),
                String.class
        );

        // Then
        assertThat(changePasswordResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturn400ForInvalidVerificationToken() {
        // When
        ResponseEntity<String> verifyResponse = restTemplate.getForEntity(
                baseUrl + "/verify?token=invalidtoken", String.class);

        // Then
        assertThat(verifyResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}

