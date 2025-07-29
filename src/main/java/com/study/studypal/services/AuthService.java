package com.study.studypal.services;

import com.study.studypal.dtos.Auth.request.*;
import com.study.studypal.dtos.Auth.response.GenerateAccessTokenResponseDto;
import com.study.studypal.dtos.Auth.response.LoginResponseDto;
import com.study.studypal.dtos.Shared.ActionResponseDto;

public interface AuthService {
    LoginResponseDto loginWithCredentials(LoginWithCredentialsRequestDto request);
    LoginResponseDto loginWithProvider(LoginWithProviderRequestDto request);
    ActionResponseDto validateRegisterInfo(ValidateRegisterInfoRequestDto request);
    ActionResponseDto registerWithCredentials(RegisterWithCredentialsRequestDto request);
    ActionResponseDto sendResetPasswordCode(String email);
    ActionResponseDto resetPassword(ResetPasswordRequestDto request);
    GenerateAccessTokenResponseDto generateAccessToken(String refreshToken);
}