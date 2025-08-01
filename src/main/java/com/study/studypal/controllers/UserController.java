package com.study.studypal.controllers;

import com.study.studypal.dtos.Shared.ActionResponseDto;
import com.study.studypal.dtos.User.request.UpdateUserRequestDto;
import com.study.studypal.dtos.User.response.ListUserResponseDto;
import com.study.studypal.dtos.User.response.UserDetailResponseDto;
import com.study.studypal.dtos.User.response.UserSummaryResponseDto;
import com.study.studypal.exceptions.ErrorResponse;
import com.study.studypal.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get user's profile.")
    @ApiResponse(responseCode = "200", description = "Get successfully.")
    @ApiResponse(responseCode = "404", description = "Not found.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<UserDetailResponseDto> getUserProfile(@AuthenticationPrincipal UUID userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @GetMapping("/summary")
    @Operation(summary = "Get user's summary profile.")
    @ApiResponse(responseCode = "200", description = "Get successfully.")
    @ApiResponse(responseCode = "404", description = "Not found.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<UserSummaryResponseDto> getUserSummaryProfile(@AuthenticationPrincipal UUID userId) {
        return ResponseEntity.ok(userService.getUserSummaryProfile(userId));
    }

    @GetMapping("/search")
    @Operation(summary = "Search for users by name.")
    @ApiResponse(responseCode = "200", description = "Search successfully.")
    public ResponseEntity<ListUserResponseDto> searchUsersByName(@AuthenticationPrincipal UUID userId,
                                                                 @RequestParam String keyword,
                                                                 @RequestParam(required = false) UUID cursor,
                                                                 @RequestParam(defaultValue = "10") @Positive int size) {
        return ResponseEntity.ok(userService.searchUsersByName(userId, keyword, cursor, size));
    }

    @PatchMapping
    @Operation(summary = "Update user's profile.")
    @ApiResponse(responseCode = "200", description = "Update successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid request body.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<UserDetailResponseDto> updateUser(@AuthenticationPrincipal UUID userId,
                                                            @Valid @RequestBody UpdateUserRequestDto request) {
        return ResponseEntity.ok(userService.updateUser(userId, request));
    }

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload user's avatar.")
    @ApiResponse(responseCode = "200", description = "Upload successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid request body.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ActionResponseDto> uploadUserAvatar(@AuthenticationPrincipal UUID userId,
                                                              @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(userService.uploadUserAvatar(userId, file));
    }
}
