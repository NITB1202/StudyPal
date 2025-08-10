package com.study.studypal.unit;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.dto.FileResponseDto;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.exception.code.FileErrorCode;
import com.study.studypal.common.service.FileService;
import com.study.studypal.common.util.FileUtils;
import com.study.studypal.user.dto.request.UpdateUserRequestDto;
import com.study.studypal.user.dto.response.ListUserResponseDto;
import com.study.studypal.user.dto.response.UserDetailResponseDto;
import com.study.studypal.user.dto.response.UserSummaryResponseDto;
import com.study.studypal.user.entity.User;
import com.study.studypal.user.exception.UserErrorCode;
import com.study.studypal.user.repository.UserRepository;
import com.study.studypal.user.service.api.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private FileService fileService;

    @InjectMocks
    private UserServiceImpl userService;

    private UUID userId;
    private String userName;
    private User user;

    @BeforeEach
    public void setup() {
        userId = UUID.randomUUID();
        userName = "user_" + (int)(Math.random() * 10000);

        user = User.builder()
                .id(userId)
                .name(userName)
                .build();
    }

    //getUserSummaryProfile
    @Test
    public void getUserSummaryProfile_whenUserExists_shouldReturnUserSummaryDto() {
        UserSummaryResponseDto userDto = UserSummaryResponseDto.builder()
                .id(userId)
                .name(userName)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserSummaryResponseDto.class)).thenReturn(userDto);

        UserSummaryResponseDto result = userService.getUserSummaryProfile(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals(userName, result.getName());

        verify(userRepository, times(1)).findById(userId);
        verify(modelMapper, times(1)).map(user, UserSummaryResponseDto.class);
    }

    @Test
    public void getUserSummaryProfile_whenUserNotFound_shouldThrowBaseException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        BaseException thrown = assertThrows(BaseException.class, () -> {
            userService.getUserSummaryProfile(userId);
        });

        assertEquals(UserErrorCode.USER_NOT_FOUND, thrown.getErrorCode());

        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(modelMapper);
    }


    //getUserProfile
    @Test
    void getUserProfile_whenUserExists_thenReturnUserDetailResponseDto() {
        UserDetailResponseDto userDto = UserDetailResponseDto.builder()
                .id(userId)
                .name(userName)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDetailResponseDto.class)).thenReturn(userDto);

        UserDetailResponseDto result = userService.getUserProfile(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals(userName, result.getName());

        verify(userRepository).findById(userId);
        verify(modelMapper).map(user, UserDetailResponseDto.class);
    }

    @Test
    void getUserProfile_whenUserNotFound_thenThrowBaseException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        BaseException ex = assertThrows(BaseException.class, () -> userService.getUserProfile(userId));
        assertEquals(UserErrorCode.USER_NOT_FOUND, ex.getErrorCode());

        verify(userRepository).findById(userId);
        verifyNoInteractions(modelMapper);
    }


    //searchUsersByName
    @Test
    void searchUsersByName_whenResultSizeEqualsPageSize_shouldReturnNextCursor() {
        // Arrange
        UUID cursor = UUID.randomUUID();
        int size = 2;

        String keyword = "TestUser";
        String handledKeyword = keyword.toLowerCase().trim();

        // Mock data: 2 users to match size = 2
        User user1 = User.builder().id(UUID.randomUUID()).name("TestUser1").build();
        User user2 = User.builder().id(UUID.randomUUID()).name("TestUser2").build();
        List<User> users = List.of(user1, user2);

        // Mock mapped DTO list
        UserSummaryResponseDto dto1 = UserSummaryResponseDto.builder().id(user1.getId()).name(user1.getName()).build();
        UserSummaryResponseDto dto2 = UserSummaryResponseDto.builder().id(user2.getId()).name(user2.getName()).build();
        List<UserSummaryResponseDto> dtoList = List.of(dto1, dto2);

        long totalCount = 2L;

        when(userRepository.searchByNameWithCursor(eq(userId), eq(handledKeyword), eq(cursor), any(Pageable.class)))
                .thenReturn(users);

        when(modelMapper.map(users, new TypeToken<List<UserSummaryResponseDto>>() {}.getType()))
                .thenReturn(dtoList);

        when(userRepository.countByName(userId, handledKeyword))
                .thenReturn(totalCount);

        // Act
        ListUserResponseDto result = userService.searchUsersByName(userId, keyword, cursor, size);

        // Assert
        assertNotNull(result);
        assertEquals(totalCount, result.getTotal());
        assertEquals(dtoList, result.getUsers());
        assertEquals(user2.getId(), result.getNextCursor());

        verify(userRepository).searchByNameWithCursor(eq(userId), eq(handledKeyword), eq(cursor), any(Pageable.class));
        verify(modelMapper).map(users, new TypeToken<List<UserSummaryResponseDto>>() {}.getType());
        verify(userRepository).countByName(userId, handledKeyword);
    }

    @Test
    void searchUsersByName_whenResultSizeLessThanPageSize_shouldReturnNullNextCursor() {
        // Arrange
        UUID cursor = UUID.randomUUID();
        int size = 2;

        String keyword = "anotherUser";
        String handledKeyword = keyword.toLowerCase().trim();

        User user1 = User.builder().id(UUID.randomUUID()).name("AnotherUser1").build();
        List<User> users = List.of(user1);

        UserSummaryResponseDto dto1 = UserSummaryResponseDto.builder().id(user1.getId()).name(user1.getName()).build();
        List<UserSummaryResponseDto> dtoList = List.of(dto1);

        long totalCount = 1L;

        when(userRepository.searchByNameWithCursor(eq(userId), eq(handledKeyword), eq(cursor), any(Pageable.class)))
                .thenReturn(users);

        when(modelMapper.map(users, new TypeToken<List<UserSummaryResponseDto>>() {}.getType()))
                .thenReturn(dtoList);

        when(userRepository.countByName(userId, handledKeyword))
                .thenReturn(totalCount);

        // Act
        ListUserResponseDto result = userService.searchUsersByName(userId, keyword, cursor, size);

        // Assert
        assertNotNull(result);
        assertEquals(totalCount, result.getTotal());
        assertEquals(dtoList, result.getUsers());
        assertNull(result.getNextCursor());

        verify(userRepository).searchByNameWithCursor(eq(userId), eq(handledKeyword), eq(cursor), any(Pageable.class));
        verify(modelMapper).map(users, new TypeToken<List<UserSummaryResponseDto>>() {}.getType());
        verify(userRepository).countByName(userId, handledKeyword);
    }

    @Test
    void searchUsersByName_whenResultIsEmpty_shouldReturnEmptyResult() {
        // Arrange
        UUID cursor = UUID.randomUUID();
        int size = 2;

        String keyword = "empty";
        String handledKeyword = keyword.toLowerCase().trim();

        List<User> users = Collections.emptyList();
        List<UserSummaryResponseDto> dtoList = Collections.emptyList();
        long totalCount = 0L;

        when(userRepository.searchByNameWithCursor(eq(userId), eq(handledKeyword), eq(cursor), any(Pageable.class)))
                .thenReturn(users);

        when(modelMapper.map(users, new TypeToken<List<UserSummaryResponseDto>>() {}.getType()))
                .thenReturn(dtoList);

        when(userRepository.countByName(userId, handledKeyword))
                .thenReturn(totalCount);

        // Act
        ListUserResponseDto result = userService.searchUsersByName(userId, keyword, cursor, size);

        // Assert
        assertNotNull(result);
        assertEquals(totalCount, result.getTotal());
        assertTrue(result.getUsers().isEmpty());
        assertNull(result.getNextCursor());

        verify(userRepository).searchByNameWithCursor(eq(userId), eq(handledKeyword), eq(cursor), any(Pageable.class));
        verify(modelMapper).map(users, new TypeToken<List<UserSummaryResponseDto>>() {}.getType());
        verify(userRepository).countByName(userId, handledKeyword);
    }


    //updateUser
    @Test
    void updateUser_whenUserExists_shouldUpdateAndReturnUserDetail() {
        // Arrange
        String newName = "NewName";

        UpdateUserRequestDto updateRequest = UpdateUserRequestDto.builder()
                .name(newName)
                .build();

        UserDetailResponseDto userDto = UserDetailResponseDto.builder()
                .id(userId)
                .name(newName)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doAnswer(invocation -> {
            UpdateUserRequestDto req = invocation.getArgument(0);
            User u = invocation.getArgument(1);
            u.setName(req.getName());
            return null;
        }).when(modelMapper).map(updateRequest, user);

        when(userRepository.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserDetailResponseDto.class)).thenReturn(userDto);

        // Act
        UserDetailResponseDto result = userService.updateUser(userId, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("NewName", result.getName());

        verify(userRepository).findById(userId);
        verify(modelMapper).map(updateRequest, user);
        verify(userRepository).save(user);
        verify(modelMapper).map(user, UserDetailResponseDto.class);
    }

    @Test
    void updateUser_whenUserNotFound_shouldThrowBaseException() {
        // Arrange
        String newName = "NewName";

        UpdateUserRequestDto updateRequest = UpdateUserRequestDto.builder()
                .name(newName)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        BaseException ex = assertThrows(BaseException.class, () -> userService.updateUser(userId, updateRequest));
        assertEquals(UserErrorCode.USER_NOT_FOUND, ex.getErrorCode());

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(modelMapper);
    }


    //uploadUserAvatar
    @Test
    void uploadUserAvatar_whenFileIsNotImage_shouldThrowInvalidImageFileException() {
        MockMultipartFile mockFile = new MockMultipartFile(
                "avatar", "avatar.png", "image/png", "dummy image content".getBytes()
        );

        try (MockedStatic<FileUtils> utilities = mockStatic(FileUtils.class)) {
            utilities.when(() -> FileUtils.isImage(any())).thenReturn(false);

            BaseException ex = assertThrows(BaseException.class,
                    () -> userService.uploadUserAvatar(userId, mockFile));
            assertEquals(FileErrorCode.INVALID_IMAGE_FILE, ex.getErrorCode());

            utilities.verify(() -> FileUtils.isImage(any()), times(1));
            verifyNoInteractions(fileService, userRepository);
        }
    }

    @Test
    void uploadUserAvatar_whenUserExistsAndFileValid_shouldUploadSuccessfully() throws IOException {
        MockMultipartFile mockFile = new MockMultipartFile(
                "avatar", "avatar.png", "image/png", "dummy image content".getBytes()
        );

        try (MockedStatic<FileUtils> utilities = mockStatic(FileUtils.class)) {
            utilities.when(() -> FileUtils.isImage(any())).thenReturn(true);

            // Mock fileService uploadFile returns URL
            FileResponseDto uploadResponse = mock(FileResponseDto.class);
            when(uploadResponse.getUrl()).thenReturn("http://avatar.url/image.png");
            when(fileService.uploadFile(eq("users"), eq(userId.toString()), any())).thenReturn(uploadResponse);

            User user = User.builder().id(userId).build();
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);

            // Act
            ActionResponseDto response = userService.uploadUserAvatar(userId, mockFile);

            // Assert
            assertTrue(response.isSuccess());
            assertEquals("Uploaded avatar successfully.", response.getMessage());
            assertEquals("http://avatar.url/image.png", user.getAvatarUrl());

            utilities.verify(() -> FileUtils.isImage(mockFile), times(1));
            verify(fileService).uploadFile("users", userId.toString(), mockFile.getBytes());
            verify(userRepository).findById(userId);
            verify(userRepository).save(user);
        }
    }

    @Test
    void uploadUserAvatar_whenUserNotFound_shouldThrowUserNotFoundException() throws IOException {
        MockMultipartFile mockFile = new MockMultipartFile(
                "avatar", "avatar.png", "image/png", "dummy image content".getBytes()
        );

        try (MockedStatic<FileUtils> utilities = mockStatic(FileUtils.class)) {
            utilities.when(() -> FileUtils.isImage(any())).thenReturn(true);

            FileResponseDto uploadResponse = mock(FileResponseDto.class);
            when(uploadResponse.getUrl()).thenReturn("http://avatar.url/image.png");
            when(fileService.uploadFile(eq("users"), eq(userId.toString()), any())).thenReturn(uploadResponse);

            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            BaseException ex = assertThrows(BaseException.class,
                    () -> userService.uploadUserAvatar(userId, mockFile));
            assertEquals(UserErrorCode.USER_NOT_FOUND, ex.getErrorCode());

            utilities.verify(() -> FileUtils.isImage(mockFile), times(1));
            verify(fileService).uploadFile("users", userId.toString(), mockFile.getBytes());
            verify(userRepository).findById(userId);
            verify(userRepository, never()).save(any());
        }
    }

    @Test
    void uploadUserAvatar_whenFileBytesThrowIOException_shouldThrowInvalidFileContentException() throws IOException {
        MockMultipartFile mockFile = new MockMultipartFile(
                "avatar", "avatar.png", "image/png", "dummy image content".getBytes()
        );

        try (MockedStatic<FileUtils> utilities = mockStatic(FileUtils.class)) {
            utilities.when(() -> FileUtils.isImage(any())).thenReturn(true);

            // Mock MultipartFile.getBytes() throws IOException
            MockMultipartFile mockFileWithException = spy(mockFile);
            doThrow(IOException.class).when(mockFileWithException).getBytes();

            BaseException ex = assertThrows(BaseException.class,
                    () -> userService.uploadUserAvatar(userId, mockFileWithException));
            assertEquals(FileErrorCode.INVALID_FILE_CONTENT, ex.getErrorCode());

            utilities.verify(() -> FileUtils.isImage(mockFileWithException), times(1));
            verifyNoInteractions(fileService, userRepository);
        }
    }
}
