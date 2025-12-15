package com.study.studypal.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.service.FileService;
import com.study.studypal.user.dto.request.UpdateUserRequestDto;
import com.study.studypal.user.dto.response.ListUserResponseDto;
import com.study.studypal.user.dto.response.UserDetailResponseDto;
import com.study.studypal.user.dto.response.UserPreviewResponseDto;
import com.study.studypal.user.dto.response.UserResponseDto;
import com.study.studypal.user.dto.response.UserSummaryResponseDto;
import com.study.studypal.user.entity.User;
import com.study.studypal.user.exception.UserErrorCode;
import com.study.studypal.user.factory.UserFactory;
import com.study.studypal.user.repository.UserRepository;
import com.study.studypal.user.service.api.impl.UserServiceImpl;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;

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
        assertThrows(BaseException.class, () -> userService.getUserSummaryProfile(userId));

    assertEquals(UserErrorCode.USER_NOT_FOUND, thrown.getErrorCode());

    verify(userRepository, times(1)).findById(userId);
    verifyNoInteractions(modelMapper);
  }

  // getUserProfile
  @Test
  void getUserProfile_whenUserExists_thenReturnUserDetailResponseDto() {
    UserDetailResponseDto userDto = UserFactory.createUserDetailResponseDto(userId, userName);

    when(userRepository.getUserProfile(userId)).thenReturn(Optional.of(userDto));

    UserDetailResponseDto result = userService.getUserProfile(userId);

    assertNotNull(result);
    assertEquals(userId, result.getId());
    assertEquals(userName, result.getName());

    verify(userRepository).getUserProfile(userId);
  }

  @Test
  void getUserProfile_whenUserNotFound_thenThrowUserNotFoundException() {
    when(userRepository.getUserProfile(userId)).thenReturn(Optional.empty());

    BaseException ex = assertThrows(BaseException.class, () -> userService.getUserProfile(userId));
    assertEquals(UserErrorCode.USER_NOT_FOUND, ex.getErrorCode());

    verify(userRepository).getUserProfile(userId);
  }

  // searchUsersByNameOrEmail
  @Test
  void searchUsersByNameOrEmail_whenResultSizeEqualsPageSize_shouldReturnNextCursor() {
    // Arrange
    UUID cursor = UUID.randomUUID();
    int size = 2;

    String keyword = "TestUser";
    String handledKeyword = keyword.toLowerCase().trim();

    // Mock data: 2 users to match size = 2
    UserPreviewResponseDto user1 = UserFactory.createUserPreviewResponseDto("TestUser1");
    UserPreviewResponseDto user2 = UserFactory.createUserPreviewResponseDto("TestUser2");
    List<UserPreviewResponseDto> users = List.of(user1, user2);

    long totalCount = 2L;

    when(userRepository.searchByNameOrEmailWithCursor(
            eq(userId), eq(handledKeyword), eq(cursor), any(Pageable.class)))
        .thenReturn(users);
    when(userRepository.countByNameOrEmail(userId, handledKeyword)).thenReturn(totalCount);

    // Act
    ListUserResponseDto result =
        userService.searchUsersByNameOrEmail(userId, keyword, cursor, size);

    // Assert
    assertNotNull(result);
    assertEquals(totalCount, result.getTotal());
    assertEquals(users, result.getUsers());
    assertEquals(user2.getId(), result.getNextCursor());

    verify(userRepository)
        .searchByNameOrEmailWithCursor(
            eq(userId), eq(handledKeyword), eq(cursor), any(Pageable.class));
    verify(userRepository).countByNameOrEmail(userId, handledKeyword);
  }

  @Test
  void searchUsersByNameOrEmail_whenResultSizeLessThanPageSize_shouldReturnNullNextCursor() {
    // Arrange
    UUID cursor = UUID.randomUUID();
    int size = 2;

    String keyword = "anotherUser";
    String handledKeyword = keyword.toLowerCase().trim();

    UserPreviewResponseDto user1 = UserFactory.createUserPreviewResponseDto("AnotherUser1");
    List<UserPreviewResponseDto> users = List.of(user1);

    long totalCount = 1L;

    when(userRepository.searchByNameOrEmailWithCursor(
            eq(userId), eq(handledKeyword), eq(cursor), any(Pageable.class)))
        .thenReturn(users);
    when(userRepository.countByNameOrEmail(userId, handledKeyword)).thenReturn(totalCount);

    // Act
    ListUserResponseDto result =
        userService.searchUsersByNameOrEmail(userId, keyword, cursor, size);

    // Assert
    assertNotNull(result);
    assertEquals(totalCount, result.getTotal());
    assertEquals(users, result.getUsers());
    assertNull(result.getNextCursor());

    verify(userRepository)
        .searchByNameOrEmailWithCursor(
            eq(userId), eq(handledKeyword), eq(cursor), any(Pageable.class));
    verify(userRepository).countByNameOrEmail(userId, handledKeyword);
  }

  @Test
  void searchUsersByNameOrEmail_whenResultIsEmpty_shouldReturnEmptyResult() {
    // Arrange
    UUID cursor = UUID.randomUUID();
    int size = 2;

    String keyword = "empty";
    String handledKeyword = keyword.toLowerCase().trim();

    List<UserPreviewResponseDto> users = Collections.emptyList();
    long totalCount = 0L;

    when(userRepository.searchByNameOrEmailWithCursor(
            eq(userId), eq(handledKeyword), eq(cursor), any(Pageable.class)))
        .thenReturn(users);
    when(userRepository.countByNameOrEmail(userId, handledKeyword)).thenReturn(totalCount);

    // Act
    ListUserResponseDto result =
        userService.searchUsersByNameOrEmail(userId, keyword, cursor, size);

    // Assert
    assertNotNull(result);
    assertEquals(totalCount, result.getTotal());
    assertTrue(result.getUsers().isEmpty());
    assertNull(result.getNextCursor());

    verify(userRepository)
        .searchByNameOrEmailWithCursor(
            eq(userId), eq(handledKeyword), eq(cursor), any(Pageable.class));
    verify(userRepository).countByNameOrEmail(userId, handledKeyword);
  }

  // updateUser
  @Test
  void updateUser_whenUserExists_shouldUpdateAndReturnUserDetail() {
    // Arrange
    String newName = "NewName";
    UpdateUserRequestDto updateRequest = UserFactory.createUpdateUserRequestDto(newName);
    UserResponseDto userDto = UserFactory.createUserResponseDto(userId, newName);

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
    when(modelMapper.map(user, UserResponseDto.class)).thenReturn(userDto);

    // Act
    UserResponseDto result = userService.updateUser(userId, updateRequest, null);

    // Assert
    assertNotNull(result);
    assertEquals(userId, result.getId());
    assertEquals("NewName", result.getName());

    verify(userRepository).findById(userId);
    verify(modelMapper).map(updateRequest, user);
    verify(userRepository).save(user);
    verify(modelMapper).map(user, UserResponseDto.class);
  }

  @Test
  void updateUser_whenUserNotFound_shouldThrowUserNotFoundException() {
    // Arrange
    String newName = "NewName";
    UpdateUserRequestDto updateRequest = UserFactory.createUpdateUserRequestDto(newName);

    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // Act & Assert
    BaseException ex =
        assertThrows(
            BaseException.class, () -> userService.updateUser(userId, updateRequest, null));
    assertEquals(UserErrorCode.USER_NOT_FOUND, ex.getErrorCode());

    verify(userRepository).findById(userId);
    verifyNoMoreInteractions(userRepository);
    verifyNoInteractions(modelMapper);
  }
}
