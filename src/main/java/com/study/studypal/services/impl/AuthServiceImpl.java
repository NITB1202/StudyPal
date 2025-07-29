package com.study.studypal.services.impl;

import com.study.studypal.dtos.Auth.request.*;
import com.study.studypal.dtos.Auth.response.GenerateAccessTokenResponseDto;
import com.study.studypal.dtos.Auth.response.LoginResponseDto;
import com.study.studypal.dtos.Shared.ActionResponseDto;
import com.study.studypal.services.AuthService;

public class AuthServiceImpl implements AuthService {
    @Override
    public LoginResponseDto loginWithCredentials(LoginWithCredentialsRequestDto request) {
        return null;
    }

    @Override
    public LoginResponseDto loginWithProvider(LoginWithProviderRequestDto request) {
        return null;
    }

    @Override
    public ActionResponseDto validateRegisterInfo(ValidateRegisterInfoRequestDto request) {
        return null;
    }

    @Override
    public ActionResponseDto registerWithCredentials(RegisterWithCredentialsRequestDto request) {
        return null;
    }

    @Override
    public ActionResponseDto sendResetPasswordCode(String email) {
        return null;
    }

    @Override
    public ActionResponseDto resetPassword(ResetPasswordRequestDto request) {
        return null;
    }

    @Override
    public GenerateAccessTokenResponseDto generateAccessToken(String refreshToken) {
        return null;
    }
}
