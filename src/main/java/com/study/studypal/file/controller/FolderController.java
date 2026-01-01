package com.study.studypal.file.controller;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.annotation.BadRequestApiResponse;
import com.study.studypal.common.exception.annotation.NotFoundApiResponse;
import com.study.studypal.common.exception.annotation.UnauthorizedApiResponse;
import com.study.studypal.file.dto.folder.request.CreateFolderRequestDto;
import com.study.studypal.file.dto.folder.request.UpdateFolderRequestDto;
import com.study.studypal.file.dto.folder.response.FolderDetailResponseDto;
import com.study.studypal.file.dto.folder.response.FolderResponseDto;
import com.study.studypal.file.dto.folder.response.ListFolderResponseDto;
import com.study.studypal.file.dto.usage.UsageResponseDto;
import com.study.studypal.file.service.api.FolderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FolderController {
  private final FolderService folderService;

  @PostMapping("/api/folders")
  @Operation(summary = "Create a new folder.")
  @ApiResponse(responseCode = "200", description = "Create successfully.")
  @UnauthorizedApiResponse
  @BadRequestApiResponse
  public ResponseEntity<FolderResponseDto> createFolder(
      @AuthenticationPrincipal UUID userId,
      @RequestParam(required = false) UUID teamId,
      @Valid @RequestBody CreateFolderRequestDto request) {
    return ResponseEntity.ok(folderService.createFolder(userId, teamId, request));
  }

  @GetMapping("/api/folders/{folderId}")
  @Operation(summary = "Get folder's details")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  @UnauthorizedApiResponse
  @NotFoundApiResponse
  public ResponseEntity<FolderDetailResponseDto> getFolderDetail(
      @AuthenticationPrincipal UUID userId, @PathVariable UUID folderId) {
    return ResponseEntity.ok(folderService.getFolderDetail(userId, folderId));
  }

  @GetMapping("/api/folders/all")
  @Operation(summary = "Get a list of folders.")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  @UnauthorizedApiResponse
  public ResponseEntity<ListFolderResponseDto> getFolders(
      @AuthenticationPrincipal UUID userId,
      @RequestParam(required = false) UUID teamId,
      @RequestParam(required = false) LocalDateTime cursor,
      @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok(folderService.getFolders(userId, teamId, cursor, size));
  }

  @PatchMapping("/api/folders/{folderId}")
  @Operation(summary = "Update a folder.")
  @ApiResponse(responseCode = "200", description = "Update successfully.")
  @UnauthorizedApiResponse
  @BadRequestApiResponse
  @NotFoundApiResponse
  public ResponseEntity<ActionResponseDto> updateFolder(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID folderId,
      @Valid @RequestBody UpdateFolderRequestDto request) {
    return ResponseEntity.ok(folderService.updateFolder(userId, folderId, request));
  }

  @DeleteMapping("/api/folders/{folderId}")
  @Operation(summary = "Delete a folder")
  @ApiResponse(responseCode = "200", description = "Delete successfully.")
  @UnauthorizedApiResponse
  @NotFoundApiResponse
  public ResponseEntity<ActionResponseDto> deleteFolder(
      @AuthenticationPrincipal UUID userId, @PathVariable UUID folderId) {
    return ResponseEntity.ok(folderService.deleteFolder(userId, folderId));
  }

  @GetMapping("/api/folders/usage")
  @Operation(summary = "Get user's storage usage")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  public ResponseEntity<UsageResponseDto> getUserUsage(@AuthenticationPrincipal UUID userId) {
    return ResponseEntity.ok(folderService.getUserUsage(userId));
  }

  @GetMapping("/api/teams/{teamId}/folders/usage")
  @Operation(summary = "Get team's storage usage")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  @UnauthorizedApiResponse
  public ResponseEntity<UsageResponseDto> getTeamUsage(
      @AuthenticationPrincipal UUID userId, @PathVariable UUID teamId) {
    return ResponseEntity.ok(folderService.getTeamUsage(userId, teamId));
  }
}
