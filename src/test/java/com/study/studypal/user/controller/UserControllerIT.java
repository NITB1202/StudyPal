package com.study.studypal.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.studypal.auth.enums.AccountRole;
import com.study.studypal.auth.util.JwtUtils;
import com.study.studypal.common.cache.CacheNames;
import com.study.studypal.common.config.TestConfig;
import com.study.studypal.common.dto.FileResponseDto;
import com.study.studypal.common.exception.code.FileErrorCode;
import com.study.studypal.common.factory.FileFactory;
import com.study.studypal.common.service.FileService;
import com.study.studypal.common.util.CacheKeyUtils;
import com.study.studypal.user.dto.request.UpdateUserRequestDto;
import com.study.studypal.user.entity.User;
import com.study.studypal.user.factory.UserFactory;
import com.study.studypal.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Import(TestConfig.class)
@ActiveProfiles("test")
class UserControllerIT {
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
    private MockMvc mockMvc;

    @Autowired
    CacheManager cacheManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileService fileService;

    private UUID currentUserId;
    private String accessToken;

    @BeforeEach
    void setUp() {
        User currentUser = userRepository.save(UserFactory.createForSave("Current"));
        currentUserId = currentUser.getId();

        accessToken = JwtUtils.generateAccessToken(currentUserId, AccountRole.USER);
        cacheManager.getCache(CacheNames.ACCESS_TOKENS).put(CacheKeyUtils.of(currentUserId), accessToken);

        userRepository.save(UserFactory.createForSave("Alice"));
        userRepository.save(UserFactory.createForSave("alex"));
    }

    //getUserProfile
    @Test
    void getUserProfile_whenUserExists_shouldReturnUserProfile() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Current"));
    }

    @Test
    void getUserProfile_whenTokenMissing_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUserProfile_whenTokenNotInCache_shouldReturnInvalidAccessToken() throws Exception {
        //Remove token from cache to make token invalid
        cacheManager.getCache(CacheNames.ACCESS_TOKENS).evict(CacheKeyUtils.of(currentUserId));

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("INVALID_ACCESS_TOKEN"));
    }


    //searchUsersByName
    @Test
    void searchUsersByName_whenMatchesFound_shouldReturnUserList() throws Exception {
        mockMvc.perform(get("/api/users/search")
                        .param("keyword", "al")
                        .param("size", "2")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.users.length()").value(2))
                .andExpect(jsonPath("$.users[*].name",
                        containsInAnyOrder("Alice", "alex")))
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.nextCursor").exists());
    }

    @Test
    void searchUsersByName_whenNoMatches_shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/users/search")
                        .param("keyword", "zzz")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users").isEmpty())
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.nextCursor").isEmpty());
    }


    //updateUser
    @Test
    void updateUser_whenValidRequest_shouldReturnUpdatedUser() throws Exception {
        String updatedName = "Updated name";
        UpdateUserRequestDto request = UserFactory.createUpdateUserRequestDto(updatedName);

        mockMvc.perform(patch("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(currentUserId.toString()))
                .andExpect(jsonPath("$.name").value(updatedName));

        //Verify database updated
        User updated = userRepository.findById(currentUserId).orElseThrow();
        assertEquals(updatedName, updated.getName());
    }

    @Test
    void updateUser_whenInvalidRequest_shouldReturn400() throws Exception {
        UpdateUserRequestDto request = UserFactory.createUpdateUserRequestDto("");

        mockMvc.perform(patch("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").exists());
    }


    //updateAvatar
    @Test
    void uploadUserAvatar_whenValidImage_shouldReturnSuccess() throws Exception {
        String newAvatarUrl = "http://avatar.url/avatar.png";
        MockMultipartFile file = FileFactory.createImageFile();
        FileResponseDto uploadResponse = mock(FileResponseDto.class);

        //Mock FileService
        when(uploadResponse.getUrl()).thenReturn(newAvatarUrl);
        when(fileService.uploadFile(anyString(), eq(currentUserId.toString()),any(byte[].class))).thenReturn(uploadResponse);

        mockMvc.perform(multipart("/api/users/avatar")
                        .file(file)
                        .with(request -> { request.setMethod("POST"); return request; })
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        //Check database
        User updated = userRepository.findById(currentUserId).orElseThrow();
        assertEquals(newAvatarUrl, updated.getAvatarUrl());
    }

    @Test
    void uploadUserAvatar_whenInvalidImageFile_shouldThrowInvalidImageFile() throws Exception {
        MockMultipartFile file = FileFactory.createRawFile();

        mockMvc.perform(multipart("/api/users/avatar")
                        .file(file)
                        .with(request -> { request.setMethod("POST"); return request; })
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.errorCode").value(FileErrorCode.INVALID_IMAGE_FILE.getCode()));
    }
}
