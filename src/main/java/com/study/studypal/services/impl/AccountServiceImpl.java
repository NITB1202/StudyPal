package com.study.studypal.services.impl;

import com.study.studypal.dtos.Shared.ActionResponseDto;
import com.study.studypal.entities.Account;
import com.study.studypal.enums.AccountRole;
import com.study.studypal.enums.AuthProvider;
import com.study.studypal.enums.ExternalAuthProvider;
import com.study.studypal.exceptions.BusinessException;
import com.study.studypal.exceptions.NotFoundException;
import com.study.studypal.repositories.AccountRepository;
import com.study.studypal.services.AccountService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void registerWithCredentials(UUID userId, String email, String password) {
        String hashedPassword = passwordEncoder.encode(password);

        Account account = Account.builder()
                .userId(userId)
                .email(email)
                .hashedPassword(hashedPassword)
                .role(AccountRole.USER)
                .providers(List.of(AuthProvider.LOCAL))
                .build();

        accountRepository.save(account);
    }

    @Override
    public void registerWithProvider(UUID userId, String email, ExternalAuthProvider provider) {
        AuthProvider authProvider = toAuthProvider(provider);

        Account account = Account.builder()
                .userId(userId)
                .email(email)
                .role(AccountRole.USER)
                .providers(List.of(authProvider))
                .build();

        accountRepository.save(account);
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
    public Account getAccountById(UUID id) {
        return accountRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Account with id " + id + " not found.")
        );
    }

    @Override
    public Account loginWithCredentials(String email, String password) {
        Account account = accountRepository.findByEmail(email);

        if(account == null) {
            throw new NotFoundException("Email is not registered.");
        }

        if(!account.getProviders().contains(AuthProvider.LOCAL)) {
            throw new BusinessException("This account was created through a third-party login. Please sign in using your linked provider.");
        }

        if(!passwordEncoder.matches(password, account.getHashedPassword())) {
            throw new BusinessException("Incorrect password.");
        }

        account.setLastLoginAt(LocalDateTime.now());
        accountRepository.save(account);

        return account;
    }

    @Override
    public Account loginWithProvider(String email, ExternalAuthProvider provider) {
        Account account = accountRepository.findByEmail(email);
        AuthProvider authProvider = toAuthProvider(provider);

        if(!account.getProviders().contains(authProvider)) {
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

        if(account != null && account.getProviders().contains(AuthProvider.LOCAL)) {
            return ActionResponseDto.builder()
                    .success(false)
                    .message("This email is already registered.")
                    .build();
        }

        if(!validatePassword(password)) {
            return ActionResponseDto.builder()
                    .success(false)
                    .message(getPasswordRule())
                    .build();
        }

        return ActionResponseDto.builder()
                .success(true)
                .message("Validate successfully.")
                .build();
    }

    @Override
    public ActionResponseDto resetPassword(String email, String newPassword) {
        if(!validatePassword(newPassword)) {
            return ActionResponseDto.builder()
                    .success(false)
                    .message(getPasswordRule())
                    .build();
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
        //Must be at least 3 characters long and contain both letters and numbers.
        String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{3,}$";
        return password.matches(passwordRegex);
    }

    private String getPasswordRule() {
        return "Password must be at least 3 characters long and contain both letters and numbers.";
    }

    private AuthProvider toAuthProvider(ExternalAuthProvider provider) {
        return switch(provider) {
            case GOOGLE -> AuthProvider.GOOGLE;
        };
    }
}
