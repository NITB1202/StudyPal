package com.study.studypal.integration.repository;

import com.study.studypal.factory.UserFactory;
import com.study.studypal.user.entity.User;
import com.study.studypal.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class UserRepositoryIT {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("password");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    private UUID currentUserId;

    @BeforeEach
    void setUp() {
        User currentUser = userRepository.save(UserFactory.createForSave());
        currentUserId = currentUser.getId();

        userRepository.save(UserFactory.createForSave("Alice"));
        userRepository.save(UserFactory.createForSave("alex"));
        userRepository.save(UserFactory.createForSave("Bob"));
        userRepository.save(UserFactory.createForSave("Alfred"));
    }

    @Test
    void searchByNameWithCursor_shouldReturnMatchingUsersIgnoringCase_andExcludeCurrentUser() {
        List<User> results = userRepository.searchByNameWithCursor(
                currentUserId,
                "al",
                null,
                PageRequest.of(0, 10)
        );

        assertThat(results)
                .extracting(User::getName)
                .containsExactlyInAnyOrder("Alice", "alex", "Alfred");
        assertThat(results)
                .allSatisfy(user -> assertThat(user.getId()).isNotEqualTo(currentUserId));
    }

    @Test
    void searchByNameWithCursor_shouldApplyCursorPagination() {
        List<User> allResults = userRepository.searchByNameWithCursor(
                currentUserId,
                "al",
                null,
                PageRequest.of(0, 10)
        );

        UUID cursor = allResults.get(0).getId();

        List<User> pagedResults = userRepository.searchByNameWithCursor(
                currentUserId,
                "al",
                cursor,
                PageRequest.of(0, 10)
        );

        assertThat(pagedResults).hasSize(allResults.size() - 1);
        assertThat(pagedResults)
                .allSatisfy(user -> assertThat(user.getId()).isNotEqualTo(cursor));
    }

    @Test
    void searchByNameWithCursor_shouldReturnEmptyListIfNoMatch() {
        List<User> results = userRepository.searchByNameWithCursor(
                currentUserId,
                "zzz",
                null,
                PageRequest.of(0, 10)
        );

        assertThat(results).isEmpty();
    }

    @Test
    void countByName_shouldReturnNumberOfMatchesExcludingCurrentUser() {
        long count = userRepository.countByName(currentUserId, "al");
        assertThat(count).isEqualTo(3);
    }

    @Test
    void countByName_shouldReturnZeroIfNoMatch() {
        long count = userRepository.countByName(currentUserId, "zzz");
        assertThat(count).isZero();
    }
}