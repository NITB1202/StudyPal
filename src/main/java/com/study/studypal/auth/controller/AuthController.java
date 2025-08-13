package com.study.studypal.auth.controller;

import com.study.studypal.auth.dto.request.*;
import com.study.studypal.auth.dto.response.GenerateAccessTokenResponseDto;
import com.study.studypal.auth.dto.response.LoginResponseDto;
import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.ErrorResponse;
import com.study.studypal.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/cred")
    @Operation(summary = "Login with credentials.")
    @ApiResponse(responseCode = "200", description = "Login successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid request body.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<LoginResponseDto> loginWithCredentials(@Valid @RequestBody LoginWithCredentialsRequestDto request) {
        return ResponseEntity.ok(authService.loginWithCredentials(request));
    }

    @PostMapping("/prov")
    @Operation(summary = "Login with OAuth provider(Google).")
    @ApiResponse(responseCode = "200", description = "Login successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid request body.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<LoginResponseDto> loginWithProvider(@Valid @RequestBody LoginWithProviderRequestDto request) {
        return ResponseEntity.ok(authService.loginWithProvider(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout.")
    @ApiResponse(responseCode = "200", description = "Logout successfully.")
    public ResponseEntity<ActionResponseDto> logout(@AuthenticationPrincipal UUID userId) {
        return ResponseEntity.ok(authService.logout(userId));
    }

    @PostMapping("/register")
    @Operation(summary = "Register.")
    @ApiResponse(responseCode = "200", description = "Register successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid request body.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ActionResponseDto> registerWithCredentials(@Valid @RequestBody RegisterWithCredentialsRequestDto request) {
        return ResponseEntity.ok(authService.registerWithCredentials(request));
    }

    @PostMapping("/code")
    @Operation(summary = "Send the verification code to the registered email address.")
    @ApiResponse(responseCode = "200", description = "Send successfully.")
    public ResponseEntity<ActionResponseDto> sendVerificationCode(@Valid @RequestBody SendVerificationCodeRequestDto request) {
        return ResponseEntity.ok(authService.sendVerificationCode(request));
    }

    @PostMapping("/verify/register")
    @Operation(summary = "Verify registration code.")
    @ApiResponse(responseCode = "200", description = "Verify successfully.")
    public ResponseEntity<ActionResponseDto> verifyRegistrationCode(@Valid @RequestBody VerifyCodeRequestDto request) {
        return ResponseEntity.ok(authService.verifyRegistrationCode(request));
    }

    @PostMapping("/verify/reset")
    @Operation(summary = "Verify reset password code.")
    @ApiResponse(responseCode = "200", description = "Verify successfully.")
    public ResponseEntity<ActionResponseDto> verifyResetPasswordCode(@Valid @RequestBody VerifyCodeRequestDto request) {
        return ResponseEntity.ok(authService.verifyResetPasswordCode(request));
    }

    @PostMapping("/reset")
    @Operation(summary = "Reset the password.")
    @ApiResponse(responseCode = "200", description = "Reset successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid request body.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ActionResponseDto> resetPassword(@Valid @RequestBody ResetPasswordRequestDto request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }

    @PostMapping("/access")
    @Operation(summary = "Generate a new access token from refresh token.")
    @ApiResponse(responseCode = "200", description = "Generate successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid refresh token.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<GenerateAccessTokenResponseDto> generateAccessToken(@Valid @RequestBody GenerateAccessTokenRequestDto request) {
        return ResponseEntity.ok(authService.generateAccessToken(request));
    }
}
