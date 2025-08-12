package com.study.studypal.auth.service;

import com.study.studypal.auth.dto.request.*;
import com.study.studypal.auth.dto.response.GenerateAccessTokenResponseDto;
import com.study.studypal.auth.dto.response.LoginResponseDto;
import com.study.studypal.common.dto.ActionResponseDto;

import java.util.UUID;

public interface AuthService {
    LoginResponseDto loginWithCredentials(LoginWithCredentialsRequestDto request);
    LoginResponseDto loginWithProvider(LoginWithProviderRequestDto request);
    ActionResponseDto logout(UUID userId);
    ActionResponseDto registerWithCredentials(RegisterWithCredentialsRequestDto request);
    ActionResponseDto sendVerificationCode(SendVerificationCodeRequestDto request);
    ActionResponseDto verifyRegistrationCode(VerifyCodeRequestDto request);
    ActionResponseDto verifyResetPasswordCode(VerifyCodeRequestDto request);
    ActionResponseDto resetPassword(ResetPasswordRequestDto request);
    GenerateAccessTokenResponseDto generateAccessToken(GenerateAccessTokenRequestDto request);
}