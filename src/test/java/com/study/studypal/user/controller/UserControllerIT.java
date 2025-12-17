package com.study.studypal.user.controller;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.study.studypal.auth.enums.AccountRole;
import com.study.studypal.auth.factory.AccountFactory;
import com.study.studypal.auth.repository.AccountRepository;
import com.study.studypal.auth.security.JwtService;
import com.study.studypal.common.cache.CacheNames;
import com.study.studypal.common.config.TestConfig;
import com.study.studypal.common.service.FileService;
import com.study.studypal.common.util.CacheKeyUtils;
import com.study.studypal.user.entity.User;
import com.study.studypal.user.factory.UserFactory;
import com.study.studypal.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Import(TestConfig.class)
@ActiveProfiles("test")
class UserControllerIT {
  @Autowired private MockMvc mockMvc;

  @Autowired CacheManager cacheManager;

  @Autowired private UserRepository userRepository;

  @Autowired private AccountRepository accountRepository;

  @Autowired private FileService fileService;

  @Autowired private JwtService jwtService;

  private UUID currentUserId;
  private String accessToken;

  @BeforeEach
  void setUp() {
    User currentUser = UserFactory.createForSave("Current");
    User user1 = UserFactory.createForSave("Stephany");
    User user2 = UserFactory.createForSave("Stelio");

    userRepository.save(currentUser);
    userRepository.save(user1);
    userRepository.save(user2);

    currentUserId = currentUser.getId();

    accessToken = jwtService.generateAccessToken(currentUserId, AccountRole.USER);
    cacheManager
        .getCache(CacheNames.ACCESS_TOKENS)
        .put(CacheKeyUtils.of(currentUserId), accessToken);

    accountRepository.save(AccountFactory.createWithUser(currentUser));
    accountRepository.save(AccountFactory.createWithUser(user1));
    accountRepository.save(AccountFactory.createWithUser(user2));
  }

  // getUserProfile
  @Test
  void getUserProfile_whenUserExists_shouldReturnUserProfile() throws Exception {
    mockMvc
        .perform(
            get("/api/users/" + currentUserId).header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Current"));
  }

  @Test
  void getUserProfile_whenTokenMissing_shouldReturnUnauthorized() throws Exception {
    mockMvc.perform(get("/api/users")).andExpect(status().isUnauthorized());
  }

  @Test
  void getUserProfile_whenTokenNotInCache_shouldReturnInvalidAccessToken() throws Exception {
    // Remove token from cache to make token invalid
    cacheManager.getCache(CacheNames.ACCESS_TOKENS).evict(CacheKeyUtils.of(currentUserId));

    mockMvc
        .perform(get("/api/users").header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.errorCode").value("UNAUTHORIZED"));
  }

  // searchUsersByNameOrEmail
  @Test
  void searchUsersByNameOrEmail_whenMatchesFound_shouldReturnUserList() throws Exception {
    mockMvc
        .perform(
            get("/api/users/search")
                .param("keyword", "ste")
                .param("size", "2")
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.users").isArray())
        .andExpect(jsonPath("$.users.length()").value(2))
        .andExpect(jsonPath("$.users[*].name", containsInAnyOrder("Stephany", "Stelio")))
        .andExpect(jsonPath("$.total").value(2))
        .andExpect(jsonPath("$.nextCursor").exists());
  }

  @Test
  void searchUsersByNameOrEmail_whenNoMatches_shouldReturnEmptyList() throws Exception {
    mockMvc
        .perform(
            get("/api/users/search")
                .param("keyword", "zzz")
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.users").isEmpty())
        .andExpect(jsonPath("$.total").value(0))
        .andExpect(jsonPath("$.nextCursor").isEmpty());
  }
}
