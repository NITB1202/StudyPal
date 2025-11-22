package com.study.studypal.auth.service.impl;

import static com.study.studypal.auth.constant.AuthConstant.VERIFICATION_CODE_LENGTH;
import static com.study.studypal.auth.constant.AuthConstant.VERIFICATION_EMAIL_CONTENT;
import static com.study.studypal.auth.constant.AuthConstant.VERIFICATION_EMAIL_SUBJECT;

import com.study.studypal.auth.constant.AuthErrorCode;
import com.study.studypal.auth.dto.internal.OAuthUserInfo;
import com.study.studypal.auth.dto.request.GenerateAccessTokenRequestDto;
import com.study.studypal.auth.dto.request.LoginWithCredentialsRequestDto;
import com.study.studypal.auth.dto.request.LoginWithProviderRequestDto;
import com.study.studypal.auth.dto.request.RegisterWithCredentialsRequestDto;
import com.study.studypal.auth.dto.request.ResetPasswordRequestDto;
import com.study.studypal.auth.dto.request.SendVerificationCodeRequestDto;
import com.study.studypal.auth.dto.request.VerifyCodeRequestDto;
import com.study.studypal.auth.dto.response.GenerateAccessTokenResponseDto;
import com.study.studypal.auth.dto.response.LoginResponseDto;
import com.study.studypal.auth.entity.Account;
import com.study.studypal.auth.enums.VerificationType;
import com.study.studypal.auth.security.JwtService;
import com.study.studypal.auth.service.AccountService;
import com.study.studypal.auth.service.AuthService;
import com.study.studypal.common.cache.CacheNames;
import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.service.CodeService;
import com.study.studypal.common.service.MailService;
import com.study.studypal.common.util.CacheKeyUtils;
import com.study.studypal.user.service.internal.UserInternalService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
  private final AccountService accountService;
  private final UserInternalService userService;
  private final MailService mailService;
  private final CodeService codeService;
  private final JwtService jwtService;
  private final CacheManager cacheManager;
  private Cache registerCache;
  private Cache resetPasswordCache;
  private Cache verificationCodeCache;
  private Cache accessTokenCache;
  private Cache refreshTokenCache;

  @PostConstruct
  public void initCaches() {
    this.registerCache = cacheManager.getCache(CacheNames.REGISTER);
    this.resetPasswordCache = cacheManager.getCache(CacheNames.RESET_PASSWORD);
    this.verificationCodeCache = cacheManager.getCache(CacheNames.VERIFICATION_CODES);
    this.accessTokenCache = cacheManager.getCache(CacheNames.ACCESS_TOKENS);
    this.refreshTokenCache = cacheManager.getCache(CacheNames.REFRESH_TOKENS);
  }

  @Override
  public LoginResponseDto loginWithCredentials(LoginWithCredentialsRequestDto request) {
    Account account =
        accountService.loginWithCredentials(request.getEmail(), request.getPassword());
    return saveUserSession(account);
  }

  @Override
  public LoginResponseDto loginWithProvider(LoginWithProviderRequestDto request) {
    OAuthUserInfo userInfo = null;

    switch (request.getProvider()) {
      case GOOGLE -> userInfo = getUserInfoWithGoogle(request.getAccessToken());
    }

    if (userInfo == null) {
      throw new BaseException(AuthErrorCode.INVALID_ACCESS_TOKEN);
    }

    if (!accountService.isEmailRegistered(userInfo.getEmail())) {
      UUID userId = userService.createProfile(userInfo.getName(), userInfo.getPicture());
      accountService.registerWithProvider(userId, userInfo.getEmail(), request.getProvider());
    }

    Account account = accountService.loginWithProvider(userInfo.getEmail(), request.getProvider());

    return saveUserSession(account);
  }

  private OAuthUserInfo getUserInfoWithGoogle(String accessToken) {
    String googleAPIUrl = "https://www.googleapis.com/oauth2/v3/userinfo";

    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    HttpEntity<String> entity = new HttpEntity<>(headers);

    var response = restTemplate.exchange(googleAPIUrl, HttpMethod.GET, entity, Map.class);

    Map<String, String> userInfo = response.getBody();

    if (userInfo == null) {
      throw new BaseException(AuthErrorCode.OAUTH_USER_INFO_NOT_FOUND);
    }

    return OAuthUserInfo.builder()
        .id(userInfo.get("id"))
        .email(userInfo.get("email"))
        .name(userInfo.get("name"))
        .picture(userInfo.get("picture"))
        .build();
  }

  private LoginResponseDto saveUserSession(Account account) {
    UUID userId = account.getUser().getId();

    String accessToken = jwtService.generateAccessToken(userId, account.getRole());
    String refreshToken = jwtService.generateRefreshToken(userId);

    accessTokenCache.put(CacheKeyUtils.of(userId), accessToken);
    refreshTokenCache.put(CacheKeyUtils.of(userId), refreshToken);

    return LoginResponseDto.builder().accessToken(accessToken).refreshToken(refreshToken).build();
  }

  @Override
  public ActionResponseDto logout(UUID userId) {
    accessTokenCache.evict(CacheKeyUtils.of(userId));
    refreshTokenCache.evict(CacheKeyUtils.of(userId));

    return ActionResponseDto.builder().success(true).message("Successfully logged out.").build();
  }

  @Override
  public ActionResponseDto registerWithCredentials(RegisterWithCredentialsRequestDto request) {
    ActionResponseDto response =
        accountService.validateAccount(request.getEmail(), request.getPassword());

    if (response.isSuccess()) {
      registerCache.put(CacheKeyUtils.of(request.getEmail()), request);
      String verificationCode =
          generateVerificationCode(request.getEmail(), VerificationType.REGISTER);
      sendVerificationEmail(request.getEmail(), verificationCode);
    }

    return response;
  }

  @Override
  public ActionResponseDto sendVerificationCode(SendVerificationCodeRequestDto request) {
    boolean isValid = false;

    switch (request.getType()) {
      case REGISTER:
        {
          RegisterWithCredentialsRequestDto info =
              registerCache.get(
                  CacheKeyUtils.of(request.getEmail()), RegisterWithCredentialsRequestDto.class);
          if (info != null) {
            // Reset TTL when user resends verification code for a validated registration request.
            registerCache.put(CacheKeyUtils.of(request.getEmail()), info);
            isValid = true;
          }

          break;
        }
      case RESET_PASSWORD:
        {
          if (accountService.isEmailRegistered(request.getEmail())) {
            isValid = true;
          }

          break;
        }
    }

    if (isValid) {
      String verificationCode = generateVerificationCode(request.getEmail(), request.getType());
      sendVerificationEmail(request.getEmail(), verificationCode);

      return ActionResponseDto.builder()
          .success(true)
          .message("A verification code has been sent to the registered email.")
          .build();
    } else {

      return ActionResponseDto.builder().success(false).message("Email is not registered.").build();
    }
  }

  @Override
  public ActionResponseDto verifyRegistrationCode(VerifyCodeRequestDto request) {
    if (verifyCode(request.getEmail(), request.getCode(), VerificationType.REGISTER)) {
      RegisterWithCredentialsRequestDto info =
          registerCache.get(
              CacheKeyUtils.of(request.getEmail()), RegisterWithCredentialsRequestDto.class);

      if (info == null) {
        throw new BaseException(AuthErrorCode.REGISTRATION_INFO_NOT_FOUND);
      }

      if (!accountService.isEmailRegistered(request.getEmail())) {
        UUID userId = userService.createDefaultProfile(info.getName());
        accountService.registerWithCredentials(userId, request.getEmail(), info.getPassword());
      } else {
        accountService.linkLocalLogin(request.getEmail(), info.getPassword());
      }

      registerCache.evict(CacheKeyUtils.of(request.getEmail()));

      return ActionResponseDto.builder().success(true).message("Register successfully.").build();
    } else {

      return ActionResponseDto.builder()
          .success(false)
          .message("Invalid verification code.")
          .build();
    }
  }

  @Override
  public ActionResponseDto verifyResetPasswordCode(VerifyCodeRequestDto request) {
    if (verifyCode(request.getEmail(), request.getCode(), VerificationType.RESET_PASSWORD)) {
      resetPasswordCache.put(CacheKeyUtils.of(request.getEmail()), true);

      return ActionResponseDto.builder()
          .success(true)
          .message("Verify email successfully.")
          .build();
    } else {

      return ActionResponseDto.builder()
          .success(false)
          .message("Invalid verification code.")
          .build();
    }
  }

  @Override
  public ActionResponseDto resetPassword(ResetPasswordRequestDto request) {
    if (resetPasswordCache.evictIfPresent(CacheKeyUtils.of(request.getEmail()))) {
      return accountService.resetPassword(request.getEmail(), request.getNewPassword());
    } else {

      return ActionResponseDto.builder()
          .success(false)
          .message("This email isn't verified.")
          .build();
    }
  }

  @Override
  public GenerateAccessTokenResponseDto generateAccessToken(GenerateAccessTokenRequestDto request) {
    UUID userId = jwtService.extractId(request.getRefreshToken());
    Account account = accountService.getAccountByUserId(userId);

    String storedRefreshToken = refreshTokenCache.get(CacheKeyUtils.of(userId), String.class);

    if (storedRefreshToken != null && storedRefreshToken.equals(request.getRefreshToken())) {
      String newAccessToken = jwtService.generateAccessToken(userId, account.getRole());
      accessTokenCache.put(CacheKeyUtils.of(userId), newAccessToken);

      return GenerateAccessTokenResponseDto.builder().accessToken(newAccessToken).build();

    } else {
      throw new BaseException(AuthErrorCode.INVALID_REFRESH_TOKEN);
    }
  }

  private String generateVerificationCode(String email, VerificationType type) {
    String code = codeService.generateRandomCode(VERIFICATION_CODE_LENGTH);
    verificationCodeCache.put(CacheKeyUtils.of(email, type), code);
    return code;
  }

  private void sendVerificationEmail(String email, String verificationCode) {
    mailService.sendHtmlEmail(
        email,
        VERIFICATION_EMAIL_SUBJECT,
        String.format(VERIFICATION_EMAIL_CONTENT, verificationCode));
  }

  private boolean verifyCode(String email, String code, VerificationType type) {
    String storedCode = verificationCodeCache.get(CacheKeyUtils.of(email, type), String.class);

    if (storedCode == null || !storedCode.equals(code)) {
      return false;
    }

    verificationCodeCache.evict(CacheKeyUtils.of(email));
    return true;
  }
}
