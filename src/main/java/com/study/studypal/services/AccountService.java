package com.study.studypal.services;

import com.study.studypal.dtos.Shared.ActionResponseDto;
import com.study.studypal.entities.Account;
import com.study.studypal.enums.ExternalAuthProvider;

import java.util.UUID;

public interface AccountService {
    void registerWithCredentials(UUID userId, String email, String password);
    void registerWithProvider(UUID userId, String email, ExternalAuthProvider provider);
    void linkLocalLogin(String email, String password);
    Account getAccountById(UUID id);
    Account loginWithCredentials(String email, String password);
    Account loginWithProvider(String email, ExternalAuthProvider provider);
    boolean isEmailRegistered(String email);
    ActionResponseDto validateAccount(String email, String password);
    ActionResponseDto resetPassword(String email, String newPassword);
}
