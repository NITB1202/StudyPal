package com.study.studypal.auth.service.impl;

import static com.study.studypal.common.util.Constants.PASSWORD_RULE_MESSAGE;

import com.study.studypal.auth.entity.Account;
import com.study.studypal.auth.enums.AccountRole;
import com.study.studypal.auth.enums.AuthProvider;
import com.study.studypal.auth.enums.ExternalAuthProvider;
import com.study.studypal.auth.exception.AuthErrorCode;
import com.study.studypal.auth.repository.AccountRepository;
import com.study.studypal.auth.service.AccountService;
import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
  private final AccountRepository accountRepository;
  private final PasswordEncoder passwordEncoder;

  @PersistenceContext private final EntityManager entityManager;

  @Override
  public void registerWithCredentials(UUID userId, String email, String password) {
    User user = entityManager.getReference(User.class, userId);
    String hashedPassword = passwordEncoder.encode(password);

    Account account =
        Account.builder()
            .user(user)
            .email(email)
            .hashedPassword(hashedPassword)
            .role(AccountRole.USER)
            .providers(List.of(AuthProvider.LOCAL))
            .build();

    // Handle race condition
    try {
      accountRepository.save(account);
    } catch (DataIntegrityViolationException e) {
      throw new BaseException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
    }
  }

  @Override
  public void registerWithProvider(UUID userId, String email, ExternalAuthProvider provider) {
    User user = entityManager.getReference(User.class, userId);
    AuthProvider authProvider = toAuthProvider(provider);

    Account account =
        Account.builder()
            .user(user)
            .email(email)
            .role(AccountRole.USER)
            .providers(List.of(authProvider))
            .build();

    try {
      accountRepository.save(account);
    } catch (DataIntegrityViolationException e) {
      throw new BaseException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
    }
  }

  @Override
  public void linkLocalLogin(String email, String password) {
    String hashedPassword = passwordEncoder.encode(password);

    Account account = accountRepository.findByEmail(email);

    account.setHashedPassword(hashedPassword);
    account.getProviders().add(AuthProvider.LOCAL);

    accountRepository.save(account);
  }

  @Override
  public Account getAccountByUserId(UUID userId) {
    return accountRepository
        .findByUserId(userId)
        .orElseThrow(() -> new BaseException(AuthErrorCode.ACCOUNT_NOT_FOUND));
  }

  @Override
  public Account loginWithCredentials(String email, String password) {
    Account account = accountRepository.findByEmail(email);

    if (account == null) {
      throw new BaseException(AuthErrorCode.EMAIL_NOT_FOUND);
    }

    if (!account.getProviders().contains(AuthProvider.LOCAL)) {
      throw new BaseException(AuthErrorCode.AUTH_METHOD_MISMATCH);
    }

    if (!passwordEncoder.matches(password, account.getHashedPassword())) {
      throw new BaseException(AuthErrorCode.INCORRECT_PASSWORD);
    }

    account.setLastLoginAt(LocalDateTime.now());
    accountRepository.save(account);

    return account;
  }

  @Override
  public Account loginWithProvider(String email, ExternalAuthProvider provider) {
    Account account = accountRepository.findByEmail(email);
    AuthProvider authProvider = toAuthProvider(provider);

    if (!account.getProviders().contains(authProvider)) {
      account.getProviders().add(authProvider);
    }

    account.setLastLoginAt(LocalDateTime.now());
    accountRepository.save(account);

    return account;
  }

  @Override
  public boolean isEmailRegistered(String email) {
    return accountRepository.existsByEmail(email);
  }

  @Override
  public ActionResponseDto validateAccount(String email, String password) {
    Account account = accountRepository.findByEmail(email);

    if (account != null && account.getProviders().contains(AuthProvider.LOCAL)) {
      return ActionResponseDto.builder()
          .success(false)
          .message("This email is already registered.")
          .build();
    }

    if (!validatePassword(password)) {
      return ActionResponseDto.builder().success(false).message(PASSWORD_RULE_MESSAGE).build();
    }

    return ActionResponseDto.builder()
        .success(true)
        .message(
            "Validate successfully. A verification code has been sent to the registered email.")
        .build();
  }

  @Override
  public ActionResponseDto resetPassword(String email, String newPassword) {
    if (!validatePassword(newPassword)) {
      return ActionResponseDto.builder().success(false).message(PASSWORD_RULE_MESSAGE).build();
    }

    Account account = accountRepository.findByEmail(email);

    String hashedPassword = passwordEncoder.encode(newPassword);
    account.setHashedPassword(hashedPassword);

    accountRepository.save(account);

    return ActionResponseDto.builder()
        .success(true)
        .message("Reset password successfully.")
        .build();
  }

  private boolean validatePassword(String password) {
    // Must be at least 3 characters long and contain both letters and numbers.
    String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d).{3,}$";
    return password.matches(passwordRegex);
  }

  private AuthProvider toAuthProvider(ExternalAuthProvider provider) {
    return switch (provider) {
      case GOOGLE -> AuthProvider.GOOGLE;
    };
  }
}
