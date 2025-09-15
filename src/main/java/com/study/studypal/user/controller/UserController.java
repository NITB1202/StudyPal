package com.study.studypal.user.controller;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.ErrorResponse;
import com.study.studypal.user.dto.request.UpdateUserRequestDto;
import com.study.studypal.user.dto.response.ListUserResponseDto;
import com.study.studypal.user.dto.response.UserDetailResponseDto;
import com.study.studypal.user.dto.response.UserSummaryResponseDto;
import com.study.studypal.user.service.api.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;

  @GetMapping
  @Operation(summary = "Get user's profile.")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  public ResponseEntity<UserDetailResponseDto> getUserProfile(
      @AuthenticationPrincipal UUID userId) {
    return ResponseEntity.ok(userService.getUserProfile(userId));
  }

  @GetMapping("/summary")
  @Operation(summary = "Get user's summary profile.")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  public ResponseEntity<UserSummaryResponseDto> getUserSummaryProfile(
      @AuthenticationPrincipal UUID userId) {
    return ResponseEntity.ok(userService.getUserSummaryProfile(userId));
  }

  @GetMapping("/search")
  @Operation(summary = "Search for users by name.")
  @ApiResponse(responseCode = "200", description = "Search successfully.")
  public ResponseEntity<ListUserResponseDto> searchUsersByName(
      @AuthenticationPrincipal UUID userId,
      @RequestParam String keyword,
      @RequestParam(required = false) UUID cursor,
      @RequestParam(defaultValue = "10") @Positive int size) {
    return ResponseEntity.ok(userService.searchUsersByName(userId, keyword, cursor, size));
  }

  @PatchMapping
  @Operation(summary = "Update user's profile.")
  @ApiResponse(responseCode = "200", description = "Update successfully.")
  @ApiResponse(
      responseCode = "400",
      description = "Invalid request body.",
      content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  public ResponseEntity<UserDetailResponseDto> updateUser(
      @AuthenticationPrincipal UUID userId, @Valid @RequestBody UpdateUserRequestDto request) {
    return ResponseEntity.ok(userService.updateUser(userId, request));
  }

  @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "Upload user's avatar.")
  @ApiResponse(responseCode = "200", description = "Upload successfully.")
  @ApiResponse(
      responseCode = "400",
      description = "Invalid request body.",
      content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  public ResponseEntity<ActionResponseDto> uploadUserAvatar(
      @AuthenticationPrincipal UUID userId, @RequestParam("file") MultipartFile file) {
    return ResponseEntity.ok(userService.uploadUserAvatar(userId, file));
  }
}
