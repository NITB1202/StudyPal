package com.study.studypal.user.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.study.studypal.auth.factory.AccountFactory;
import com.study.studypal.auth.repository.AccountRepository;
import com.study.studypal.user.dto.response.UserPreviewResponseDto;
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
  @Autowired private AccountRepository accountRepository;

  private UUID currentUserId;

  @BeforeEach
  void setUp() {
    User currentUser = UserFactory.createForSave();
    User user1 = UserFactory.createForSave("Stephany");
    User user2 = UserFactory.createForSave("Stelio");
    User user3 = UserFactory.createForSave("Bob");
    User user4 = UserFactory.createForSave("Steamy");

    userRepository.save(currentUser);
    userRepository.save(user1);
    userRepository.save(user2);
    userRepository.save(user3);
    userRepository.save(user4);

    currentUserId = currentUser.getId();

    accountRepository.save(AccountFactory.createWithUser(currentUser));
    accountRepository.save(AccountFactory.createWithUser(user1));
    accountRepository.save(AccountFactory.createWithUser(user2));
    accountRepository.save(AccountFactory.createWithUser(user3));
    accountRepository.save(AccountFactory.createWithUser(user4));
  }

  @Test
  void
      searchByNameOrEmailWithCursor_whenKeywordMatchesIgnoringCase_shouldReturnMatchingUsersExcludingCurrentUser() {
    List<UserPreviewResponseDto> results =
        userRepository.searchByNameOrEmailWithCursor(
            currentUserId, "ste", null, PageRequest.of(0, 10));

    assertThat(results)
        .extracting(UserPreviewResponseDto::getName)
        .containsExactlyInAnyOrder("Stephany", "Stelio", "Steamy");
    assertThat(results).allSatisfy(user -> assertThat(user.getId()).isNotEqualTo(currentUserId));
  }

  @Test
  void searchByNameOrEmailWithCursor_whenCursorProvided_shouldReturnNextPage() {
    List<UserPreviewResponseDto> allResults =
        userRepository.searchByNameOrEmailWithCursor(
            currentUserId, "ste", null, PageRequest.of(0, 10));

    UUID cursor = allResults.get(0).getId();

    List<UserPreviewResponseDto> pagedResults =
        userRepository.searchByNameOrEmailWithCursor(
            currentUserId, "ste", cursor, PageRequest.of(0, 10));

    assertThat(pagedResults)
        .hasSize(allResults.size() - 1)
        .allSatisfy(user -> assertThat(user.getId()).isNotEqualTo(cursor));
  }

  @Test
  void searchByNameOrEmailWithCursor_whenNoUserMatches_shouldReturnEmptyList() {
    List<UserPreviewResponseDto> results =
        userRepository.searchByNameOrEmailWithCursor(
            currentUserId, "zzz", null, PageRequest.of(0, 10));

    assertThat(results).isEmpty();
  }

  @Test
  void countByNameOrEmail_whenKeywordMatches_shouldReturnNumberOfMatchesExcludingCurrentUser() {
    long count = userRepository.countByNameOrEmail(currentUserId, "ste");
    assertThat(count).isEqualTo(3);
  }

  @Test
  void countByNameOrEmail_whenNoUserMatches_shouldReturnZero() {
    long count = userRepository.countByNameOrEmail(currentUserId, "zzz");
    assertThat(count).isZero();
  }
}
