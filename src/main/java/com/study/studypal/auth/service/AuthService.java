package com.study.studypal.auth.service;

import com.study.studypal.auth.dto.request.*;
import com.study.studypal.auth.dto.response.GenerateAccessTokenResponseDto;
import com.study.studypal.auth.dto.response.LoginResponseDto;
import com.study.studypal.dtos.Shared.ActionResponseDto;

import java.util.UUID;

public interface AuthService {
    LoginResponseDto loginWithCredentials(LoginWithCredentialsRequestDto request);
    LoginResponseDto loginWithProvider(LoginWithProviderRequestDto request);
    ActionResponseDto logout(UUID userId);
    ActionResponseDto validateRegisterInfo(ValidateRegisterInfoRequestDto request);
    ActionResponseDto registerWithCredentials(RegisterWithCredentialsRequestDto request);
    ActionResponseDto sendResetPasswordCode(String email);
    ActionResponseDto resetPassword(ResetPasswordRequestDto request);
    GenerateAccessTokenResponseDto generateAccessToken(String refreshToken);
}