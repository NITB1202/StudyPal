package com.study.studypal.controllers;

import com.study.studypal.dtos.Auth.request.*;
import com.study.studypal.dtos.Auth.response.GenerateAccessTokenResponseDto;
import com.study.studypal.dtos.Auth.response.LoginResponseDto;
import com.study.studypal.dtos.Shared.ActionResponseDto;
import com.study.studypal.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
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

    @PostMapping("/validate")
    @Operation(summary = "Validate account information before registering.")
    @ApiResponse(responseCode = "200", description = "Validate successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid request body.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ActionResponseDto> validateRegisterInfo(@Valid @RequestBody ValidateRegisterInfoRequestDto request) {
        return ResponseEntity.ok(authService.validateRegisterInfo(request));
    }

    @PostMapping("/register")
    @Operation(summary = "Register.")
    @ApiResponse(responseCode = "200", description = "Register successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid request body.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ActionResponseDto> registerWithCredentials(@Valid @RequestBody RegisterWithCredentialsRequestDto request) {
        return ResponseEntity.ok(authService.registerWithCredentials(request));
    }

    @GetMapping("/reset")
    @Operation(summary = "Send a verification code to the email address to reset the password.")
    @ApiResponse(responseCode = "200", description = "Send successfully.")
    public ResponseEntity<ActionResponseDto> sendResetPasswordCode(@Email @RequestParam String email) {
        return ResponseEntity.ok(authService.sendResetPasswordCode(email));
    }

    @PostMapping("/reset")
    @Operation(summary = "Reset the password.")
    @ApiResponse(responseCode = "200", description = "Reset successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid request body.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ActionResponseDto> resetPassword(@Valid @RequestBody ResetPasswordRequestDto request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }

    @GetMapping("/access")
    @Operation(summary = "Generate a new access token from refresh token.")
    @ApiResponse(responseCode = "200", description = "Generate successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid refresh token.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<GenerateAccessTokenResponseDto> generateAccessToken(@RequestParam String refreshToken) {
        return ResponseEntity.ok(authService.generateAccessToken(refreshToken));
    }
}
