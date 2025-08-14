package com.study.studypal.integration.service;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.dto.FileResponseDto;
import com.study.studypal.common.service.FileService;
import com.study.studypal.config.TestBeansConfig;
import com.study.studypal.factory.FileFactory;
import com.study.studypal.factory.UserFactory;
import com.study.studypal.user.dto.request.UpdateUserRequestDto;
import com.study.studypal.user.dto.response.ListUserResponseDto;
import com.study.studypal.user.dto.response.UserDetailResponseDto;
import com.study.studypal.user.dto.response.UserSummaryResponseDto;
import com.study.studypal.user.entity.User;
import com.study.studypal.user.repository.UserRepository;
import com.study.studypal.user.service.api.UserService;
import com.study.studypal.user.service.api.impl.UserServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DataJpaTest
@Testcontainers
@Import(TestBeansConfig.class)
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class UserServiceIT {
    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("pass");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    private UserService userService;
    private UUID currentUserId;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, modelMapper, fileService);
        User currentUser = userRepository.save(UserFactory.createForSave());
        currentUserId = currentUser.getId();
    }

    @Test
    void getUserSummaryProfile_shouldReturnSummary() {
        UserSummaryResponseDto summary = userService.getUserSummaryProfile(currentUserId);
        assertNotNull(summary);
        assertEquals(currentUserId, summary.getId());
    }

    @Test
    void getUserProfile_shouldReturnDetail() {
        UserDetailResponseDto detail = userService.getUserProfile(currentUserId);
        assertNotNull(detail);
        assertEquals(currentUserId, detail.getId());
    }

    @Test
    void searchUsersByName_shouldReturnMatchingUsers() {
        // Arrange
        userRepository.save(UserFactory.createForSave("Alice"));
        userRepository.save(UserFactory.createForSave("alex"));
        userRepository.save(UserFactory.createForSave("Bob"));
        userRepository.save(UserFactory.createForSave("Alfred"));

        // Act
        ListUserResponseDto response = userService.searchUsersByName(currentUserId, "al", null, 10);

        // Assert
        assertNotNull(response);
        assertEquals(3, response.getUsers().size()); // Alice, alex, Alfred
        assertEquals(3, response.getTotal());
        assertNull(response.getNextCursor()); // No more items to fetch -> nextCursor = null

        List<String> names = response.getUsers().stream().map(UserSummaryResponseDto::getName).toList();
        assertTrue(names.containsAll(List.of("Alice", "alex", "Alfred")));
    }

    @Test
    void updateUser_shouldUpdateAndReturnDetail() {
        // Arrange
        String newName = "Updated name";
        UpdateUserRequestDto request = UserFactory.createUpdateUserRequestDto(newName);

        // Act
        UserDetailResponseDto updated = userService.updateUser(currentUserId, request);

        // Assert: verify the returned data
        assertNotNull(updated);
        assertEquals(currentUserId, updated.getId());
        assertEquals(newName, updated.getName());

        // Assert: verify the data in the database
        User userInDb = userRepository.findById(currentUserId).orElseThrow();
        assertEquals(newName, userInDb.getName());
    }

    @Test
    void uploadUserAvatar_whenValidImage_shouldUpdateUserAvatar() throws IOException {
        // Arrange
        MockMultipartFile file = FileFactory.createImageFile();
        String newAvatarUrl = "http://avatar.url/image.png";

        // Mock fileService uploadFile returns URL
        FileResponseDto uploadResponse = mock(FileResponseDto.class);
        when(uploadResponse.getUrl()).thenReturn(newAvatarUrl);
        when(fileService.uploadFile(anyString(), eq(currentUserId.toString()), refEq(file.getBytes())))
                .thenReturn(uploadResponse);

        // Act
        ActionResponseDto response = userService.uploadUserAvatar(currentUserId, file);

        // Assert: verify the returned data
        assertNotNull(response);
        assertTrue(response.isSuccess());

        // Assert: verify the data in the database
        User user = userRepository.findById(currentUserId).orElseThrow();
        assertEquals(newAvatarUrl, user.getAvatarUrl());

        // Verify fileService is called
        verify(fileService, times(1)).uploadFile(anyString(), eq(currentUserId.toString()), refEq(file.getBytes()));
    }
}