package com.study.studypal.auth.service.impl;

import com.study.studypal.auth.dto.internal.OAuthUserInfoDto;
import com.study.studypal.auth.dto.request.*;
import com.study.studypal.auth.dto.response.GenerateAccessTokenResponseDto;
import com.study.studypal.auth.dto.response.LoginResponseDto;
import com.study.studypal.auth.service.AccountService;
import com.study.studypal.auth.service.AuthService;
import com.study.studypal.common.cache.CacheNames;
import com.study.studypal.common.service.CodeService;
import com.study.studypal.common.service.MailService;
import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.auth.entity.Account;
import com.study.studypal.auth.enums.VerificationType;
import com.study.studypal.common.exception.BusinessException;
import com.study.studypal.common.exception.UnauthorizedException;
import com.study.studypal.common.util.JwtUtils;
import com.study.studypal.user.service.internal.UserInternalService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AccountService accountService;
    private final UserInternalService userService;
    private final MailService mailService;
    private final CodeService codeService;
    private final CacheManager cacheManager;

    private final Cache accessTokenCache = cacheManager.getCache(CacheNames.ACCESS_TOKENS);
    private final Cache refreshTokenCache = cacheManager.getCache(CacheNames.REFRESH_TOKENS);

    @Override
    public LoginResponseDto loginWithCredentials(LoginWithCredentialsRequestDto request) {
        Account account = accountService.loginWithCredentials(request.getEmail(), request.getPassword());
        return saveUserSession(account);
    }

    @Override
    public LoginResponseDto loginWithProvider(LoginWithProviderRequestDto request) {
        OAuthUserInfoDto userInfo = null;

        switch(request.getProvider()) {
            case GOOGLE -> userInfo = getUserInfoWithGoogle(request.getAccessToken());
        }

        if(userInfo == null) {
            throw new BusinessException("Invalid access token.");
        }

        if(!accountService.isEmailRegistered(userInfo.getEmail())) {
            UUID userId = userService.createProfile(userInfo.getName(), userInfo.getPicture());
            accountService.registerWithProvider(userId, userInfo.getEmail(), request.getProvider());
        }

        Account account = accountService.loginWithProvider(userInfo.getEmail(), request.getProvider());

        return saveUserSession(account);
    }

    private OAuthUserInfoDto getUserInfoWithGoogle(String accessToken) {
        String googleAPIUrl ="https://www.googleapis.com/oauth2/v3/userinfo";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        var response = restTemplate.exchange(
                googleAPIUrl,
                HttpMethod.GET,
                entity,
                Map.class
        );

        Map userInfo = response.getBody();

        return OAuthUserInfoDto.builder()
                .id((String)userInfo.get("id"))
                .email((String)userInfo.get("email"))
                .name((String)userInfo.get("name"))
                .picture((String)userInfo.get("picture"))
                .build();
    }

    private LoginResponseDto saveUserSession(Account account) {
        UUID userId = account.getUser().getId();

        String accessToken = JwtUtils.generateAccessToken(userId, account.getRole());
        String refreshToken = JwtUtils.generateRefreshToken(userId);

        String accessTokenKey = JwtUtils.getAccessTokenKey(userId);
        accessTokenCache.put(accessTokenKey, accessToken);

        String refreshTokenKey = JwtUtils.getRefreshTokenKey(userId);
        refreshTokenCache.put(refreshTokenKey, refreshToken);

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public ActionResponseDto logout(UUID userId) {
        accessTokenCache.evict(JwtUtils.getAccessTokenKey(userId));
        refreshTokenCache.evict(JwtUtils.getRefreshTokenKey(userId));

        return ActionResponseDto.builder()
                .success(true)
                .message("Successfully logged out.")
                .build();
    }

    @Override
    public ActionResponseDto validateRegisterInfo(ValidateRegisterInfoRequestDto request) {
        ActionResponseDto validateResponse = accountService.validateAccount(request.getEmail(), request.getPassword());

        if(validateResponse.isSuccess()) {
            String verificationCode = codeService.generateVerificationCode(request.getEmail(), VerificationType.REGISTER);
            mailService.sendVerificationEmail(request.getEmail(), verificationCode);

            String message = " A verification code has been sent to the registered email.";
            validateResponse.setMessage(validateResponse.getMessage() + message);
        }

        return validateResponse;
    }

    @Override
    public ActionResponseDto registerWithCredentials(RegisterWithCredentialsRequestDto request) {
        ActionResponseDto validateResponse = accountService.validateAccount(request.getEmail(), request.getPassword());

        if(!validateResponse.isSuccess()) {
            return validateResponse;
        }

        if(codeService.verifyCode(request.getEmail(), request.getVerificationCode(), VerificationType.REGISTER)) {
            if(!accountService.isEmailRegistered(request.getEmail())) {
                UUID userId = userService.createDefaultProfile(request.getName());
                accountService.registerWithCredentials(userId, request.getEmail(), request.getPassword());
            }
            else {
                accountService.linkLocalLogin(request.getEmail(), request.getPassword());
            }

            return ActionResponseDto.builder()
                    .success(true)
                    .message("Successfully registered.")
                    .build();
        }
        else {

            return ActionResponseDto.builder()
                    .success(false)
                    .message("Invalid verification code.")
                    .build();
        }
    }

    @Override
    public ActionResponseDto sendResetPasswordCode(String email) {
        if(accountService.isEmailRegistered(email)) {
            String verificationCode = codeService.generateVerificationCode(email, VerificationType.RESET_PASSWORD);
            mailService.sendVerificationEmail(email, verificationCode);

            return ActionResponseDto.builder()
                    .success(true)
                    .message("A verification code has been sent to the registered email.")
                    .build();
        }

        return ActionResponseDto.builder()
                .success(false)
                .message("Email is not registered.")
                .build();
    }

    @Override
    public ActionResponseDto resetPassword(ResetPasswordRequestDto request) {
        if(codeService.verifyCode(request.getEmail(), request.getVerificationCode(), VerificationType.RESET_PASSWORD)){
            return accountService.resetPassword(request.getEmail(), request.getNewPassword());
        }
        else{
            return ActionResponseDto.builder()
                    .success(false)
                    .message("Invalid verification code.")
                    .build();
        }
    }

    @Override
    public GenerateAccessTokenResponseDto generateAccessToken(String refreshToken) {
        UUID userId = JwtUtils.extractId(refreshToken);
        Account account = accountService.getAccountByUserId(userId);

        String storedRefreshToken = refreshTokenCache.get(JwtUtils.getRefreshTokenKey(userId), String.class);

        if (storedRefreshToken != null && storedRefreshToken.equals(refreshToken)) {
            String newAccessToken = JwtUtils.generateAccessToken(userId, account.getRole());
            accessTokenCache.put(JwtUtils.getAccessTokenKey(userId), newAccessToken);

            return GenerateAccessTokenResponseDto.builder()
                    .accessToken(newAccessToken)
                    .build();

        } else {
            throw new UnauthorizedException("Invalid refresh token.");
        }
    }
}
