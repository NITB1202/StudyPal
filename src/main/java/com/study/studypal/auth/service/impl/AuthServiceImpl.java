package com.study.studypal.auth.service.impl;

import com.study.studypal.auth.dto.internal.OAuthUserInfoDto;
import com.study.studypal.auth.dto.request.*;
import com.study.studypal.auth.dto.response.GenerateAccessTokenResponseDto;
import com.study.studypal.auth.dto.response.LoginResponseDto;
import com.study.studypal.auth.exception.AuthErrorCode;
import com.study.studypal.auth.service.AccountService;
import com.study.studypal.auth.service.AuthService;
import com.study.studypal.common.cache.CacheNames;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.service.CodeService;
import com.study.studypal.common.service.MailService;
import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.auth.entity.Account;
import com.study.studypal.auth.enums.VerificationType;
import com.study.studypal.common.util.CacheKeyUtils;
import com.study.studypal.auth.util.JwtUtils;
import com.study.studypal.user.service.internal.UserInternalService;
import jakarta.annotation.PostConstruct;
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
    private Cache registerCache;
    private Cache resetPasswordCache;
    private Cache accessTokenCache;
    private Cache refreshTokenCache;

    @PostConstruct
    public void initCaches() {
        this.registerCache = cacheManager.getCache(CacheNames.REGISTER);
        this.resetPasswordCache = cacheManager.getCache(CacheNames.RESET_PASSWORD);
        this.accessTokenCache = cacheManager.getCache(CacheNames.ACCESS_TOKENS);
        this.refreshTokenCache = cacheManager.getCache(CacheNames.REFRESH_TOKENS);
    }

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
            throw new BaseException(AuthErrorCode.INVALID_ACCESS_TOKEN);
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

        accessTokenCache.put(CacheKeyUtils.of(userId), accessToken);
        refreshTokenCache.put(CacheKeyUtils.of(userId), refreshToken);

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public ActionResponseDto logout(UUID userId) {
        accessTokenCache.evict(CacheKeyUtils.of(userId));
        refreshTokenCache.evict(CacheKeyUtils.of(userId));

        return ActionResponseDto.builder()
                .success(true)
                .message("Successfully logged out.")
                .build();
    }

    @Override
    public ActionResponseDto registerWithCredentials(RegisterWithCredentialsRequestDto request) {
        ActionResponseDto response = accountService.validateAccount(request.getEmail(), request.getPassword());

        if(response.isSuccess()) {
            registerCache.put(CacheKeyUtils.of(request.getEmail()), request);
            String verificationCode = codeService.generateVerificationCode(request.getEmail(), VerificationType.REGISTER);
            mailService.sendVerificationEmail(request.getEmail(), verificationCode);
        }

        return response;
    }

    @Override
    public ActionResponseDto sendVerificationCode(SendVerificationCodeRequestDto request) {
        boolean isValid = false;

        switch (request.getType()) {
            case REGISTER: {
                RegisterWithCredentialsRequestDto info = registerCache.get(CacheKeyUtils.of(request.getEmail()), RegisterWithCredentialsRequestDto.class);
                if(info != null) {
                    //Reset TTL when user resends verification code for a validated registration request.
                    registerCache.put(CacheKeyUtils.of(request.getEmail()), info);
                    isValid = true;
                }

                break;
            }
            case RESET_PASSWORD: {
                if(accountService.isEmailRegistered(request.getEmail())) {
                    isValid = true;
                }

                break;
            }
        }

        if(isValid) {
            String verificationCode = codeService.generateVerificationCode(request.getEmail(), request.getType());
            mailService.sendVerificationEmail(request.getEmail(), verificationCode);

            return ActionResponseDto.builder()
                    .success(true)
                    .message("A verification code has been sent to the registered email.")
                    .build();
        }
        else {

            return ActionResponseDto.builder()
                    .success(false)
                    .message("Email is not registered.")
                    .build();
        }
    }

    @Override
    public ActionResponseDto verifyRegistrationCode(VerifyCodeRequestDto request) {
        if(codeService.verifyCode(request.getEmail(), request.getCode(), VerificationType.REGISTER)) {
            RegisterWithCredentialsRequestDto info = registerCache.get(CacheKeyUtils.of(request.getEmail()), RegisterWithCredentialsRequestDto.class);

            if(info == null) {
                throw new BaseException(AuthErrorCode.REGISTRATION_INFO_NOT_FOUND);
            }

            if(!accountService.isEmailRegistered(request.getEmail())) {
                UUID userId = userService.createDefaultProfile(info.getName());
                accountService.registerWithCredentials(userId, request.getEmail(), info.getPassword());
            }
            else {
                accountService.linkLocalLogin(request.getEmail(), info.getPassword());
            }

            registerCache.evict(CacheKeyUtils.of(request.getEmail()));

            return ActionResponseDto.builder()
                    .success(true)
                    .message("Register successfully.")
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
    public ActionResponseDto verifyResetPasswordCode(VerifyCodeRequestDto request) {
        if(codeService.verifyCode(request.getEmail(), request.getCode(), VerificationType.RESET_PASSWORD)) {
            resetPasswordCache.put(CacheKeyUtils.of(request.getEmail()), true);

            return ActionResponseDto.builder()
                    .success(true)
                    .message("Verify email successfully.")
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
    public ActionResponseDto resetPassword(ResetPasswordRequestDto request) {
        if(resetPasswordCache.evictIfPresent(CacheKeyUtils.of(request.getEmail()))) {
            return accountService.resetPassword(request.getEmail(), request.getNewPassword());
        }
        else{

            return ActionResponseDto.builder()
                    .success(false)
                    .message("This email isn't verified.")
                    .build();
        }
    }

    @Override
    public GenerateAccessTokenResponseDto generateAccessToken(GenerateAccessTokenRequestDto request) {
        UUID userId = JwtUtils.extractId(request.getRefreshToken());
        Account account = accountService.getAccountByUserId(userId);

        String storedRefreshToken = refreshTokenCache.get(CacheKeyUtils.of(userId), String.class);

        if (storedRefreshToken != null && storedRefreshToken.equals(request.getRefreshToken())) {
            String newAccessToken = JwtUtils.generateAccessToken(userId, account.getRole());
            accessTokenCache.put(CacheKeyUtils.of(userId), newAccessToken);

            return GenerateAccessTokenResponseDto.builder()
                    .accessToken(newAccessToken)
                    .build();

        } else {
            throw new BaseException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }
    }
}
