package com.study.studypal.services;

import com.study.studypal.dtos.Shared.ActionResponseDto;
import com.study.studypal.entities.Account;
import com.study.studypal.enums.ExternalAuthProvider;

import java.util.UUID;

public interface AccountService {
    void registerWithCredentials(String username, String password);
    void registerWithProvider(ExternalAuthProvider provider, String providerId, String email);
    Account getAccountById(UUID id);
    Account getAccountByEmailAndPassword(String email, String password);
    Account getAccountByProviderId(ExternalAuthProvider provider, String providerId);
    boolean isAccountRegistered(ExternalAuthProvider provider, String providerId);
    ActionResponseDto isAccountRegistered(String email);
    ActionResponseDto validateAccount(String email, String password);
    ActionResponseDto resetPassword(String email, String newPassword);
}
