package com.study.studypal.auth.service;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.auth.entity.Account;
import com.study.studypal.auth.enums.ExternalAuthProvider;

import java.util.UUID;

public interface AccountService {
    void registerWithCredentials(UUID userId, String email, String password);
    void registerWithProvider(UUID userId, String email, ExternalAuthProvider provider);
    void linkLocalLogin(String email, String password);
    Account getAccountByUserId(UUID userId);
    Account loginWithCredentials(String email, String password);
    Account loginWithProvider(String email, ExternalAuthProvider provider);
    boolean isEmailRegistered(String email);
    ActionResponseDto validateAccount(String email, String password);
    ActionResponseDto resetPassword(String email, String newPassword);
}
