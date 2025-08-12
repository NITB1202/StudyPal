package com.study.studypal.auth.service;

import com.study.studypal.auth.dto.request.*;
import com.study.studypal.auth.dto.response.GenerateAccessTokenResponseDto;
import com.study.studypal.auth.dto.response.LoginResponseDto;
import com.study.studypal.auth.enums.VerificationType;
import com.study.studypal.common.dto.ActionResponseDto;

import java.util.UUID;

public interface AuthService {
    LoginResponseDto loginWithCredentials(LoginWithCredentialsRequestDto request);
    LoginResponseDto loginWithProvider(LoginWithProviderRequestDto request);
    ActionResponseDto logout(UUID userId);
    ActionResponseDto registerWithCredentials(RegisterWithCredentialsRequestDto request);
    ActionResponseDto sendVerificationCode(VerificationType type, String email);
    ActionResponseDto verifyRegistrationCode(String email, String code);
    ActionResponseDto verifyResetPasswordCode(String email, String code);
    ActionResponseDto resetPassword(ResetPasswordRequestDto request);
    GenerateAccessTokenResponseDto generateAccessToken(String refreshToken);
}