package com.study.studypal.user.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.study.studypal.user.entity.User;
import com.study.studypal.user.factory.UserFactory;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ActiveProfiles("test")
class UserRepositoryIT {
  @Autowired private UserRepository userRepository;

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
  void
      searchByNameWithCursor_whenKeywordMatchesIgnoringCase_shouldReturnMatchingUsersExcludingCurrentUser() {
    List<User> results =
        userRepository.searchByNameWithCursor(currentUserId, "al", null, PageRequest.of(0, 10));

    assertThat(results)
        .extracting(User::getName)
        .containsExactlyInAnyOrder("Alice", "alex", "Alfred");
    assertThat(results).allSatisfy(user -> assertThat(user.getId()).isNotEqualTo(currentUserId));
  }

  @Test
  void searchByNameWithCursor_whenCursorProvided_shouldReturnNextPage() {
    List<User> allResults =
        userRepository.searchByNameWithCursor(currentUserId, "al", null, PageRequest.of(0, 10));

    UUID cursor = allResults.get(0).getId();

    List<User> pagedResults =
        userRepository.searchByNameWithCursor(currentUserId, "al", cursor, PageRequest.of(0, 10));

    assertThat(pagedResults)
        .hasSize(allResults.size() - 1)
        .allSatisfy(user -> assertThat(user.getId()).isNotEqualTo(cursor));
  }

  @Test
  void searchByNameWithCursor_whenNoUserMatches_shouldReturnEmptyList() {
    List<User> results =
        userRepository.searchByNameWithCursor(currentUserId, "zzz", null, PageRequest.of(0, 10));

    assertThat(results).isEmpty();
  }

  @Test
  void countByName_whenKeywordMatches_shouldReturnNumberOfMatchesExcludingCurrentUser() {
    long count = userRepository.countByName(currentUserId, "al");
    assertThat(count).isEqualTo(3);
  }

  @Test
  void countByName_whenNoUserMatches_shouldReturnZero() {
    long count = userRepository.countByName(currentUserId, "zzz");
    assertThat(count).isZero();
  }
}
