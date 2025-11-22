package com.study.studypal.auth.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.study.studypal.auth.constant.AuthErrorCode;
import com.study.studypal.auth.entity.Account;
import com.study.studypal.auth.enums.AccountRole;
import com.study.studypal.auth.enums.AuthProvider;
import com.study.studypal.auth.enums.ExternalAuthProvider;
import com.study.studypal.auth.repository.AccountRepository;
import com.study.studypal.auth.service.impl.AccountServiceImpl;
import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
  @Mock private AccountRepository accountRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private EntityManager entityManager;

  @InjectMocks private AccountServiceImpl accountService;

  // registerWithCredentials
  @Test
  void registerWithCredentials_whenValidInput_shouldSaveAccountSuccessfully() {
    // Arrange
    UUID userId = UUID.randomUUID();
    String email = "test@example.com";
    String password = "password123";
    String hashedPassword = "hashedPassword";

    User user = new User();
    when(entityManager.getReference(User.class, userId)).thenReturn(user);
    when(passwordEncoder.encode(password)).thenReturn(hashedPassword);

    // Act
    assertDoesNotThrow(() -> accountService.registerWithCredentials(userId, email, password));

    // Assert
    ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
    verify(accountRepository).save(captor.capture());
    Account savedAccount = captor.getValue();

    assertAll(
        () -> assertEquals(user, savedAccount.getUser()),
        () -> assertEquals(email, savedAccount.getEmail()),
        () -> assertEquals(hashedPassword, savedAccount.getHashedPassword()),
        () -> assertEquals(AccountRole.USER, savedAccount.getRole()),
        () -> assertEquals(List.of(AuthProvider.LOCAL), savedAccount.getProviders()));
  }

  @Test
  void registerWithCredentials_whenEmailAlreadyExists_shouldThrowBaseException() {
    UUID userId = UUID.randomUUID();
    String email = "test@example.com";
    String password = "password123";

    User user = new User();
    when(entityManager.getReference(User.class, userId)).thenReturn(user);
    when(passwordEncoder.encode(password)).thenReturn("hashedPassword");

    // When save, throw DataIntegrityViolationException to simulate email already exists
    doThrow(DataIntegrityViolationException.class).when(accountRepository).save(any(Account.class));

    BaseException exception =
        assertThrows(
            BaseException.class,
            () -> accountService.registerWithCredentials(userId, email, password));

    assertEquals(AuthErrorCode.EMAIL_ALREADY_EXISTS, exception.getErrorCode());
  }

  // registerWithProvider
  @Test
  void registerWithProvider_whenValidInput_shouldSaveAccountSuccessfully() {
    // Arrange
    UUID userId = UUID.randomUUID();
    String email = "provider@example.com";
    ExternalAuthProvider externalProvider = ExternalAuthProvider.GOOGLE;

    User user = new User();
    when(entityManager.getReference(User.class, userId)).thenReturn(user);

    // Act
    assertDoesNotThrow(() -> accountService.registerWithProvider(userId, email, externalProvider));

    // Assert
    ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
    verify(accountRepository).save(captor.capture());
    Account savedAccount = captor.getValue();

    assertAll(
        () -> assertEquals(user, savedAccount.getUser()),
        () -> assertEquals(email, savedAccount.getEmail()),
        () -> assertEquals(AccountRole.USER, savedAccount.getRole()),
        () ->
            assertEquals(
                List.of(AuthProvider.GOOGLE),
                savedAccount.getProviders()) // chạy thật mapping private method
        );
  }

  @Test
  void registerWithProvider_whenEmailAlreadyExists_shouldThrowBaseException() {
    // Arrange
    UUID userId = UUID.randomUUID();
    String email = "duplicate@example.com";
    ExternalAuthProvider externalProvider = ExternalAuthProvider.GOOGLE;

    User user = new User();
    when(entityManager.getReference(User.class, userId)).thenReturn(user);

    // Giả lập save ném DataIntegrityViolationException
    doThrow(DataIntegrityViolationException.class).when(accountRepository).save(any(Account.class));

    // Act + Assert
    BaseException ex =
        assertThrows(
            BaseException.class,
            () -> accountService.registerWithProvider(userId, email, externalProvider));

    assertEquals(AuthErrorCode.EMAIL_ALREADY_EXISTS, ex.getErrorCode());
  }

  // linkLocalLogin
  @Test
  void linkLocalLogin_whenValidInput_shouldUpdateAccountSuccessfully() {
    // Arrange
    String email = "test@example.com";
    String password = "plainPassword";
    String hashedPassword = "hashedPassword";

    Account account = new Account();
    account.setProviders(new ArrayList<>());

    when(passwordEncoder.encode(password)).thenReturn(hashedPassword);
    when(accountRepository.findByEmail(email)).thenReturn(account);

    // Act
    assertDoesNotThrow(() -> accountService.linkLocalLogin(email, password));

    // Assert
    ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
    verify(accountRepository).save(captor.capture());
    Account savedAccount = captor.getValue();

    assertAll(
        () -> assertEquals(hashedPassword, savedAccount.getHashedPassword()),
        () -> assertTrue(savedAccount.getProviders().contains(AuthProvider.LOCAL)));
  }

  @Test
  void linkLocalLogin_whenAccountNotFound_shouldThrowException() {
    // Arrange
    String email = "notfound@example.com";
    String password = "password";

    when(accountRepository.findByEmail(email)).thenReturn(null);

    // Act + Assert
    assertThrows(NullPointerException.class, () -> accountService.linkLocalLogin(email, password));
  }

  // getAccountByUserId
  @Test
  void getAccountByUserId_whenAccountExists_shouldReturnAccount() {
    // Arrange
    UUID userId = UUID.randomUUID();
    Account account = new Account();
    when(accountRepository.findByUserId(userId)).thenReturn(Optional.of(account));

    // Act
    Account result = accountService.getAccountByUserId(userId);

    // Assert
    assertEquals(account, result);
    verify(accountRepository).findByUserId(userId);
  }

  @Test
  void getAccountByUserId_whenAccountNotFound_shouldThrowBaseException() {
    // Arrange
    UUID userId = UUID.randomUUID();
    when(accountRepository.findByUserId(userId)).thenReturn(Optional.empty());

    // Act + Assert
    BaseException ex =
        assertThrows(BaseException.class, () -> accountService.getAccountByUserId(userId));

    assertEquals(AuthErrorCode.ACCOUNT_NOT_FOUND, ex.getErrorCode());
    verify(accountRepository).findByUserId(userId);
  }

  // loginWithCredentials
  @Test
  void loginWithCredentials_whenValidInput_shouldReturnAccount() {
    // Arrange
    String email = "test@example.com";
    String password = "plainPassword";
    String hashedPassword = "hashedPassword";

    Account account = new Account();
    account.setEmail(email);
    account.setHashedPassword(hashedPassword);
    account.setProviders(new ArrayList<>(List.of(AuthProvider.LOCAL)));

    when(accountRepository.findByEmail(email)).thenReturn(account);
    when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);

    // Act
    Account result = accountService.loginWithCredentials(email, password);

    // Assert
    assertEquals(account, result);
    assertNotNull(result.getLastLoginAt());
    verify(accountRepository).save(account);
  }

  @Test
  void loginWithCredentials_whenEmailNotFound_shouldThrowException() {
    // Arrange
    String email = "notfound@example.com";
    String password = "password";

    when(accountRepository.findByEmail(email)).thenReturn(null);

    // Act + Assert
    BaseException ex =
        assertThrows(
            BaseException.class, () -> accountService.loginWithCredentials(email, password));

    assertEquals(AuthErrorCode.EMAIL_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  void loginWithCredentials_whenAuthMethodMismatch_shouldThrowException() {
    // Arrange
    String email = "test@example.com";
    String password = "password";

    Account account = new Account();
    account.setEmail(email);
    account.setProviders(new ArrayList<>(List.of(AuthProvider.GOOGLE)));

    when(accountRepository.findByEmail(email)).thenReturn(account);

    // Act + Assert
    BaseException ex =
        assertThrows(
            BaseException.class, () -> accountService.loginWithCredentials(email, password));

    assertEquals(AuthErrorCode.AUTH_METHOD_MISMATCH, ex.getErrorCode());
  }

  @Test
  void loginWithCredentials_whenIncorrectPassword_shouldThrowException() {
    // Arrange
    String email = "test@example.com";
    String password = "wrongPassword";
    String hashedPassword = "hashedPassword";

    Account account = new Account();
    account.setEmail(email);
    account.setHashedPassword(hashedPassword);
    account.setProviders(new ArrayList<>(List.of(AuthProvider.LOCAL)));

    when(accountRepository.findByEmail(email)).thenReturn(account);
    when(passwordEncoder.matches(password, hashedPassword)).thenReturn(false);

    // Act + Assert
    BaseException ex =
        assertThrows(
            BaseException.class, () -> accountService.loginWithCredentials(email, password));

    assertEquals(AuthErrorCode.INCORRECT_PASSWORD, ex.getErrorCode());
  }

  // loginWithProvider
  @Test
  void loginWithProvider_whenAccountAlreadyHasProvider_shouldNotAddDuplicate() {
    // Arrange
    String email = "test@example.com";
    ExternalAuthProvider externalProvider = ExternalAuthProvider.GOOGLE;
    AuthProvider authProvider = AuthProvider.GOOGLE;

    Account account = new Account();
    account.setEmail(email);
    account.setProviders(new ArrayList<>(List.of(authProvider)));

    when(accountRepository.findByEmail(email)).thenReturn(account);

    // Act
    Account result = accountService.loginWithProvider(email, externalProvider);

    // Assert
    assertEquals(account, result);
    assertTrue(result.getProviders().contains(authProvider));
    assertEquals(1, result.getProviders().size());
    assertNotNull(result.getLastLoginAt());
    verify(accountRepository).save(account);
  }

  @Test
  void loginWithProvider_whenAccountDoesNotHaveProvider_shouldAddProvider() {
    // Arrange
    String email = "test@example.com";
    ExternalAuthProvider externalProvider = ExternalAuthProvider.GOOGLE;
    AuthProvider authProvider = AuthProvider.GOOGLE;

    Account account = new Account();
    account.setEmail(email);
    account.setProviders(new ArrayList<>());

    when(accountRepository.findByEmail(email)).thenReturn(account);

    // Act
    Account result = accountService.loginWithProvider(email, externalProvider);

    // Assert
    assertEquals(account, result);
    assertTrue(result.getProviders().contains(authProvider));
    assertEquals(1, result.getProviders().size());
    assertNotNull(result.getLastLoginAt());
    verify(accountRepository).save(account);
  }

  @Test
  void loginWithProvider_whenAccountNotFound_shouldThrowNullPointerException() {
    // Arrange
    String email = "notfound@example.com";
    ExternalAuthProvider externalProvider = ExternalAuthProvider.GOOGLE;

    when(accountRepository.findByEmail(email)).thenReturn(null);

    // Act + Assert
    assertThrows(
        NullPointerException.class,
        () -> accountService.loginWithProvider(email, externalProvider));
  }

  // isEmailRegistered
  @Test
  void isEmailRegistered_WhenEmailExists_ShouldReturnTrue() {
    // Given
    String email = "test@example.com";
    when(accountRepository.existsByEmail(email)).thenReturn(true);

    // When
    boolean result = accountService.isEmailRegistered(email);

    // Then
    assertTrue(result);
    verify(accountRepository, times(1)).existsByEmail(email);
  }

  @Test
  void isEmailRegistered_WhenEmailDoesNotExist_ShouldReturnFalse() {
    // Given
    String email = "notfound@example.com";
    when(accountRepository.existsByEmail(email)).thenReturn(false);

    // When
    boolean result = accountService.isEmailRegistered(email);

    // Then
    assertFalse(result);
    verify(accountRepository, times(1)).existsByEmail(email);
  }

  // validateAccount
  @Test
  void validateAccount_WhenEmailAlreadyExistsWithLocalProvider_ShouldReturnFail() {
    // Given
    String email = "test@example.com";
    String password = "ValidPass123!";
    Account account = new Account();
    account.setProviders(List.of(AuthProvider.LOCAL));

    when(accountRepository.findByEmail(email)).thenReturn(account);

    // When
    ActionResponseDto result = accountService.validateAccount(email, password);

    // Then
    assertFalse(result.isSuccess());
    assertEquals("This email is already registered.", result.getMessage());
    verify(accountRepository, times(1)).findByEmail(email);
  }

  @Test
  void validateAccount_whenEmailAlreadyExistsWithOtherProvider_ShouldReturnSuccess() {
    // Given
    String email = "test@example.com";
    String password = "ValidPass123!";
    Account account = new Account();
    account.setProviders(List.of(AuthProvider.GOOGLE));

    when(accountRepository.findByEmail(email)).thenReturn(account);

    // When
    ActionResponseDto result = accountService.validateAccount(email, password);

    // Then
    assertTrue(result.isSuccess());
    assertEquals(
        "Validate successfully. A verification code has been sent to the registered email.",
        result.getMessage());
    verify(accountRepository, times(1)).findByEmail(email);
  }

  @Test
  void validateAccount_WhenPasswordInvalid_ShouldReturnFailWithPasswordRule() {
    // Given
    String email = "new@example.com";
    String password = "123";

    when(accountRepository.findByEmail(email)).thenReturn(null);

    // When
    ActionResponseDto result = accountService.validateAccount(email, password);

    // Then
    assertFalse(result.isSuccess());
    assertEquals(
        "Password must be at least 3 characters long and contain both letters and numbers.",
        result.getMessage());
    verify(accountRepository, times(1)).findByEmail(email);
  }

  @Test
  void validateAccount_WhenNewEmailAndValidPassword_ShouldReturnSuccess() {
    // Given
    String email = "new@example.com";
    String password = "ValidPass123!";

    when(accountRepository.findByEmail(email)).thenReturn(null);

    // When
    ActionResponseDto result = accountService.validateAccount(email, password);

    // Then
    assertTrue(result.isSuccess());
    assertEquals(
        "Validate successfully. A verification code has been sent to the registered email.",
        result.getMessage());
    verify(accountRepository, times(1)).findByEmail(email);
  }

  // resetPassword
  @Test
  void resetPassword_WhenPasswordInvalid_ShouldReturnFail() {
    // Given
    String email = "test@example.com";
    String newPassword = "123";

    // When
    ActionResponseDto result = accountService.resetPassword(email, newPassword);

    // Then
    assertFalse(result.isSuccess());
    assertEquals(
        "Password must be at least 3 characters long and contain both letters and numbers.",
        result.getMessage());
    verify(accountRepository, never()).save(any());
  }

  @Test
  void resetPassword_WhenPasswordValid_ShouldEncodeAndSaveAndReturnSuccess() {
    // Given
    String email = "test@example.com";
    String newPassword = "Password123";
    String hashedPassword = "hashedPwd";

    Account account = new Account();
    account.setEmail(email);

    when(accountRepository.findByEmail(email)).thenReturn(account);
    when(passwordEncoder.encode(newPassword)).thenReturn(hashedPassword);

    ActionResponseDto result = accountService.resetPassword(email, newPassword);

    // Then
    assertTrue(result.isSuccess());
    assertEquals("Reset password successfully.", result.getMessage());
    assertEquals(hashedPassword, account.getHashedPassword());
    verify(accountRepository, times(1)).save(account);
  }
}
