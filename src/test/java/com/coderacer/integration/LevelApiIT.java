package com.coderacer.integration;

import com.coderacer.dto.LevelDTO;
import com.coderacer.dto.LevelModifyDTO;
import com.coderacer.enums.Difficulty;
import com.coderacer.enums.ProgrammingLanguage;
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
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;

    @Autowired
    private com.coderacer.repository.LevelRepository levelRepository;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/levels";
        levelRepository.deleteAll();
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

        // When - Create level
        ResponseEntity<LevelDTO> createResponse = restTemplate.postForEntity(
                baseUrl, newLevel, LevelDTO.class);

        // Then - Verify creation
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().id()).isNotNull();
        assertThat(createResponse.getBody().codeSnippet()).isEqualTo(newLevel.codeSnippet());
        assertThat(createResponse.getBody().language()).isEqualTo(newLevel.language());
        assertThat(createResponse.getBody().difficulty()).isEqualTo(newLevel.difficulty());
        assertThat(createResponse.getBody().tags()).containsExactlyInAnyOrder("beginner", "hello");

        // When - Retrieve level
        UUID levelId = createResponse.getBody().id();
        ResponseEntity<LevelDTO> getResponse = restTemplate.getForEntity(
                baseUrl + "/" + levelId, LevelDTO.class);

        // Then - Verify retrieval
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isEqualTo(createResponse.getBody());
    }

    @Test
    void shouldGetAllLevels() {
        // Given - Create two levels
        LevelModifyDTO level1 = new LevelModifyDTO(
                "public class Test1 { public static void main(String[] args) { System.out.println(\"Test1\"); } }",
                ProgrammingLanguage.JAVA,
                Difficulty.EASY,
                Arrays.asList("test")
        );
        LevelModifyDTO level2 = new LevelModifyDTO(
                "public class Test2 { public static void main(String[] args) { System.out.println(\"Test2\"); } }",
                ProgrammingLanguage.JAVA,
                Difficulty.MEDIUM,
                Arrays.asList("test")
        );

        restTemplate.postForEntity(baseUrl, level1, LevelDTO.class);
        restTemplate.postForEntity(baseUrl, level2, LevelDTO.class);

        // When
        ResponseEntity<LevelDTO[]> response = restTemplate.getForEntity(baseUrl, LevelDTO[].class);

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
                "public class JavaTest { public static void main(String[] args) { System.out.println(\"Java\"); } }",
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

        restTemplate.postForEntity(baseUrl, javaLevel, LevelDTO.class);
        restTemplate.postForEntity(baseUrl, pythonLevel, LevelDTO.class);

        // When
        ResponseEntity<LevelDTO[]> response = restTemplate.getForEntity(
                baseUrl + "/language/JAVA", LevelDTO[].class);

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

        restTemplate.postForEntity(baseUrl, easyLevel, LevelDTO.class);
        restTemplate.postForEntity(baseUrl, hardLevel, LevelDTO.class);

        // When
        ResponseEntity<LevelDTO[]> response = restTemplate.getForEntity(
                baseUrl + "/difficulty/EASY", LevelDTO[].class);

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

        restTemplate.postForEntity(baseUrl, level1, LevelDTO.class);
        restTemplate.postForEntity(baseUrl, level2, LevelDTO.class);

        // When
        ResponseEntity<LevelDTO> response = restTemplate.getForEntity(
                baseUrl + "/random?language=JAVA&difficulty=EASY", LevelDTO.class);

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

        ResponseEntity<LevelDTO> createResponse = restTemplate.postForEntity(
                baseUrl, originalLevel, LevelDTO.class);
        UUID levelId = createResponse.getBody().id();

        // When - Update the level
        LevelModifyDTO updatedLevel = new LevelModifyDTO(
                "public class Updated { public static void main(String[] args) { System.out.println(\"Updated\"); } }",
                ProgrammingLanguage.JAVA,
                Difficulty.MEDIUM,
                Arrays.asList("updated")
        );

        ResponseEntity<LevelDTO> updateResponse = restTemplate.exchange(
                baseUrl + "/" + levelId,
                HttpMethod.PUT,
                new HttpEntity<>(updatedLevel),
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

        ResponseEntity<LevelDTO> createResponse = restTemplate.postForEntity(
                baseUrl, level, LevelDTO.class);
        UUID levelId = createResponse.getBody().id();

        // When
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                baseUrl + "/" + levelId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        // Then
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify level is deleted
        ResponseEntity<String> getResponse = restTemplate.getForEntity(
                baseUrl + "/" + levelId, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturn404ForNonExistentLevel() {
        // When
        UUID nonExistentId = UUID.randomUUID();
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/" + nonExistentId, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturn404ForRandomLevelWithNoMatches() {
        // When - Try to get random level with no data
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/random?language=JAVA&difficulty=EASY", String.class);

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
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl, invalidLevel, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}