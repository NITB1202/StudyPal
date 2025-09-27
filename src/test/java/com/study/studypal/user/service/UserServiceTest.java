package com.study.studypal.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.dto.FileResponse;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.exception.code.FileErrorCode;
import com.study.studypal.common.factory.FileFactory;
import com.study.studypal.common.service.FileService;
import com.study.studypal.common.util.FileUtils;
import com.study.studypal.user.dto.request.UpdateUserRequestDto;
import com.study.studypal.user.dto.response.ListUserResponseDto;
import com.study.studypal.user.dto.response.UserDetailResponseDto;
import com.study.studypal.user.dto.response.UserSummaryResponseDto;
import com.study.studypal.user.entity.User;
import com.study.studypal.user.exception.UserErrorCode;
import com.study.studypal.user.factory.UserFactory;
import com.study.studypal.user.repository.UserRepository;
import com.study.studypal.user.service.api.impl.UserServiceImpl;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  @Mock private UserRepository userRepository;

  @Mock private ModelMapper modelMapper;

  @Mock private FileService fileService;

  @InjectMocks private UserServiceImpl userService;

  private UUID userId;
  private String userName;
  private User user;

  @BeforeEach
  void setup() {
    user = UserFactory.createWithId();
    userId = user.getId();
    userName = user.getName();
  }

  // getUserSummaryProfile
  @Test
  void getUserSummaryProfile_whenUserExists_shouldReturnUserSummaryDto() {
    UserSummaryResponseDto userDto = UserFactory.createUserSummaryResponseDto(userId, userName);

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
  void getUserSummaryProfile_whenUserNotFound_shouldThrowBaseException() {
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    BaseException thrown =
        assertThrows(
            BaseException.class,
            () -> {
              userService.getUserSummaryProfile(userId);
            });

    assertEquals(UserErrorCode.USER_NOT_FOUND, thrown.getErrorCode());

    verify(userRepository, times(1)).findById(userId);
    verifyNoInteractions(modelMapper);
  }

  // getUserProfile
  @Test
  void getUserProfile_whenUserExists_thenReturnUserDetailResponseDto() {
    UserDetailResponseDto userDto = UserFactory.createUserDetailResponseDto(userId, userName);

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
  void getUserProfile_whenUserNotFound_thenThrowUserNotFoundException() {
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    BaseException ex = assertThrows(BaseException.class, () -> userService.getUserProfile(userId));
    assertEquals(UserErrorCode.USER_NOT_FOUND, ex.getErrorCode());

    verify(userRepository).findById(userId);
    verifyNoInteractions(modelMapper);
  }

  // searchUsersByName
  @Test
  void searchUsersByName_whenResultSizeEqualsPageSize_shouldReturnNextCursor() {
    // Arrange
    UUID cursor = UUID.randomUUID();
    int size = 2;

    String keyword = "TestUser";
    String handledKeyword = keyword.toLowerCase().trim();

    // Mock data: 2 users to match size = 2
    User user1 = UserFactory.createWithId("TestUser1");
    User user2 = UserFactory.createWithId("TestUser2");
    List<User> users = List.of(user1, user2);

    // Mock mapped DTO list
    UserSummaryResponseDto dto1 =
        UserFactory.createUserSummaryResponseDto(user1.getId(), user1.getName());
    UserSummaryResponseDto dto2 =
        UserFactory.createUserSummaryResponseDto(user2.getId(), user2.getName());
    List<UserSummaryResponseDto> dtoList = List.of(dto1, dto2);

    long totalCount = 2L;

    when(userRepository.searchByNameWithCursor(
            eq(userId), eq(handledKeyword), eq(cursor), any(Pageable.class)))
        .thenReturn(users);

    when(modelMapper.map(users, new TypeToken<List<UserSummaryResponseDto>>() {}.getType()))
        .thenReturn(dtoList);

    when(userRepository.countByName(userId, handledKeyword)).thenReturn(totalCount);

    // Act
    ListUserResponseDto result = userService.searchUsersByName(userId, keyword, cursor, size);

    // Assert
    assertNotNull(result);
    assertEquals(totalCount, result.getTotal());
    assertEquals(dtoList, result.getUsers());
    assertEquals(user2.getId(), result.getNextCursor());

    verify(userRepository)
        .searchByNameWithCursor(eq(userId), eq(handledKeyword), eq(cursor), any(Pageable.class));
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

    User user1 = UserFactory.createWithId("AnotherUser1");
    List<User> users = List.of(user1);

    UserSummaryResponseDto dto1 =
        UserFactory.createUserSummaryResponseDto(user1.getId(), user1.getName());
    List<UserSummaryResponseDto> dtoList = List.of(dto1);

    long totalCount = 1L;

    when(userRepository.searchByNameWithCursor(
            eq(userId), eq(handledKeyword), eq(cursor), any(Pageable.class)))
        .thenReturn(users);

    when(modelMapper.map(users, new TypeToken<List<UserSummaryResponseDto>>() {}.getType()))
        .thenReturn(dtoList);

    when(userRepository.countByName(userId, handledKeyword)).thenReturn(totalCount);

    // Act
    ListUserResponseDto result = userService.searchUsersByName(userId, keyword, cursor, size);

    // Assert
    assertNotNull(result);
    assertEquals(totalCount, result.getTotal());
    assertEquals(dtoList, result.getUsers());
    assertNull(result.getNextCursor());

    verify(userRepository)
        .searchByNameWithCursor(eq(userId), eq(handledKeyword), eq(cursor), any(Pageable.class));
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

    when(userRepository.searchByNameWithCursor(
            eq(userId), eq(handledKeyword), eq(cursor), any(Pageable.class)))
        .thenReturn(users);

    when(modelMapper.map(users, new TypeToken<List<UserSummaryResponseDto>>() {}.getType()))
        .thenReturn(dtoList);

    when(userRepository.countByName(userId, handledKeyword)).thenReturn(totalCount);

    // Act
    ListUserResponseDto result = userService.searchUsersByName(userId, keyword, cursor, size);

    // Assert
    assertNotNull(result);
    assertEquals(totalCount, result.getTotal());
    assertTrue(result.getUsers().isEmpty());
    assertNull(result.getNextCursor());

    verify(userRepository)
        .searchByNameWithCursor(eq(userId), eq(handledKeyword), eq(cursor), any(Pageable.class));
    verify(modelMapper).map(users, new TypeToken<List<UserSummaryResponseDto>>() {}.getType());
    verify(userRepository).countByName(userId, handledKeyword);
  }

  // updateUser
  @Test
  void updateUser_whenUserExists_shouldUpdateAndReturnUserDetail() {
    // Arrange
    String newName = "NewName";
    UpdateUserRequestDto updateRequest = UserFactory.createUpdateUserRequestDto(newName);
    UserDetailResponseDto userDto = UserFactory.createUserDetailResponseDto(userId, newName);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    doAnswer(
            invocation -> {
              UpdateUserRequestDto req = invocation.getArgument(0);
              User u = invocation.getArgument(1);
              u.setName(req.getName());
              return null;
            })
        .when(modelMapper)
        .map(updateRequest, user);

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
  void updateUser_whenUserNotFound_shouldThrowUserNotFoundException() {
    // Arrange
    String newName = "NewName";
    UpdateUserRequestDto updateRequest = UserFactory.createUpdateUserRequestDto(newName);

    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // Act & Assert
    BaseException ex =
        assertThrows(BaseException.class, () -> userService.updateUser(userId, updateRequest));
    assertEquals(UserErrorCode.USER_NOT_FOUND, ex.getErrorCode());

    verify(userRepository).findById(userId);
    verifyNoMoreInteractions(userRepository);
    verifyNoInteractions(modelMapper);
  }

  // uploadUserAvatar
  @Test
  void uploadUserAvatar_whenFileIsNotImage_shouldThrowInvalidImageFileException() {
    MockMultipartFile mockFile = FileFactory.createRawFile();

    try (MockedStatic<FileUtils> utilities = mockStatic(FileUtils.class)) {
      utilities.when(() -> FileUtils.isImage(any())).thenReturn(false);

      BaseException ex =
          assertThrows(BaseException.class, () -> userService.uploadUserAvatar(userId, mockFile));
      assertEquals(FileErrorCode.INVALID_IMAGE_FILE, ex.getErrorCode());

      utilities.verify(() -> FileUtils.isImage(any()), times(1));
      verifyNoInteractions(fileService, userRepository);
    }
  }

  @Test
  void uploadUserAvatar_whenUserExistsAndFileValid_shouldUploadSuccessfully() throws IOException {
    MockMultipartFile mockFile = FileFactory.createImageFile();
    String newAvatarUrl = "http://avatar.url/image.png";

    try (MockedStatic<FileUtils> utilities = mockStatic(FileUtils.class)) {
      utilities.when(() -> FileUtils.isImage(any())).thenReturn(true);

      // Mock fileService uploadFile returns URL
      FileResponse uploadResponse = mock(FileResponse.class);
      when(uploadResponse.getUrl()).thenReturn(newAvatarUrl);
      when(fileService.uploadFile(anyString(), eq(userId.toString()), any(byte[].class)))
          .thenReturn(uploadResponse);
      when(userRepository.findById(userId)).thenReturn(Optional.of(user));
      when(userRepository.save(any(User.class))).thenReturn(user);

      // Act
      ActionResponseDto response = userService.uploadUserAvatar(userId, mockFile);

      // Assert
      assertTrue(response.isSuccess());
      assertEquals(newAvatarUrl, user.getAvatarUrl());

      utilities.verify(() -> FileUtils.isImage(mockFile), times(1));
      verify(fileService).uploadFile(anyString(), eq(userId.toString()), eq(mockFile.getBytes()));
      verify(userRepository).findById(userId);
      verify(userRepository).save(user);
    }
  }

  @Test
  void uploadUserAvatar_whenUserNotFound_shouldThrowUserNotFoundException() throws IOException {
    MockMultipartFile mockFile = FileFactory.createImageFile();
    String newAvatarUrl = "http://avatar.url/image.png";

    try (MockedStatic<FileUtils> utilities = mockStatic(FileUtils.class)) {
      utilities.when(() -> FileUtils.isImage(any())).thenReturn(true);

      FileResponse uploadResponse = mock(FileResponse.class);
      when(uploadResponse.getUrl()).thenReturn(newAvatarUrl);
      when(fileService.uploadFile(anyString(), eq(userId.toString()), any(byte[].class)))
          .thenReturn(uploadResponse);

      when(userRepository.findById(userId)).thenReturn(Optional.empty());

      BaseException ex =
          assertThrows(BaseException.class, () -> userService.uploadUserAvatar(userId, mockFile));
      assertEquals(UserErrorCode.USER_NOT_FOUND, ex.getErrorCode());

      utilities.verify(() -> FileUtils.isImage(mockFile), times(1));
      verify(fileService).uploadFile(anyString(), eq(userId.toString()), eq(mockFile.getBytes()));
      verify(userRepository).findById(userId);
      verify(userRepository, never()).save(any());
    }
  }

  @Test
  void uploadUserAvatar_whenFileBytesThrowIOException_shouldThrowInvalidFileContentException()
      throws IOException {
    MockMultipartFile mockFile = FileFactory.createRawFile();

    try (MockedStatic<FileUtils> utilities = mockStatic(FileUtils.class)) {
      utilities.when(() -> FileUtils.isImage(any())).thenReturn(true);

      // Mock MultipartFile.getBytes() throws IOException
      MockMultipartFile mockFileWithException = spy(mockFile);
      doThrow(IOException.class).when(mockFileWithException).getBytes();

      BaseException ex =
          assertThrows(
              BaseException.class,
              () -> userService.uploadUserAvatar(userId, mockFileWithException));
      assertEquals(FileErrorCode.INVALID_FILE_CONTENT, ex.getErrorCode());

      utilities.verify(() -> FileUtils.isImage(mockFileWithException), times(1));
      verifyNoInteractions(fileService, userRepository);
    }
  }
}
