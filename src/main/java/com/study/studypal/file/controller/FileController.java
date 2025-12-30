package com.study.studypal.file.controller;

import static com.study.studypal.common.util.Constants.DEFAULT_PAGE_SIZE;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.annotation.BadRequestApiResponse;
import com.study.studypal.common.exception.annotation.NotFoundApiResponse;
import com.study.studypal.common.exception.annotation.UnauthorizedApiResponse;
import com.study.studypal.file.dto.file.request.UpdateFileRequestDto;
import com.study.studypal.file.dto.file.response.FileDetailResponseDto;
import com.study.studypal.file.dto.file.response.FileResponseDto;
import com.study.studypal.file.dto.file.response.ListDeletedFileResponseDto;
import com.study.studypal.file.dto.file.response.ListFileResponseDto;
import com.study.studypal.file.service.api.UserFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class FileController {
  private final UserFileService fileService;

  @PostMapping(
      value = "/api/folders/{folderId}/files",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "Upload a file (document or image) to a specific folder.")
  @ApiResponse(responseCode = "200", description = "Upload successfully.")
  @BadRequestApiResponse
  @UnauthorizedApiResponse
  @NotFoundApiResponse
  public ResponseEntity<FileResponseDto> uploadFile(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID folderId,
      @RequestParam(required = false) String name,
      @RequestPart(value = "file") MultipartFile file) {
    return ResponseEntity.ok(fileService.uploadFile(userId, folderId, name, file));
  }

  @GetMapping("/api/files/{fileId}")
  @Operation(summary = "Get file's details")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  @UnauthorizedApiResponse
  @NotFoundApiResponse
  public ResponseEntity<FileDetailResponseDto> getFileDetail(
      @AuthenticationPrincipal UUID userId, @PathVariable UUID fileId) {
    return ResponseEntity.ok(fileService.getFileDetail(userId, fileId));
  }

  @GetMapping("/api/folders/{folderId}/files/all")
  @Operation(summary = "Get a list of files in the folder.")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  @UnauthorizedApiResponse
  public ResponseEntity<ListFileResponseDto> getFiles(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID folderId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime cursor,
      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) @Positive int size) {
    return ResponseEntity.ok(fileService.getFiles(userId, folderId, cursor, size));
  }

  @GetMapping("/api/folders/{folderId}/files/search")
  @Operation(summary = "Search for files by name.")
  @ApiResponse(responseCode = "200", description = "Search successfully.")
  @UnauthorizedApiResponse
  public ResponseEntity<ListFileResponseDto> searchFilesByName(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID folderId,
      @RequestParam String keyword,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime cursor,
      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) @Positive int size) {
    return ResponseEntity.ok(
        fileService.searchFilesByName(userId, folderId, keyword, cursor, size));
  }

  @GetMapping("/api/files/deleted")
  @Operation(summary = "Get a list of deleted files.")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  @UnauthorizedApiResponse
  public ResponseEntity<ListDeletedFileResponseDto> getDeletedFiles(
      @AuthenticationPrincipal UUID userId,
      @RequestParam(required = false) UUID teamId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime cursor,
      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) @Positive int size) {
    return ResponseEntity.ok(fileService.getDeletedFiles(userId, teamId, cursor, size));
  }

  @PatchMapping("/api/files/{fileId}")
  @Operation(summary = "Update a file.")
  @ApiResponse(responseCode = "200", description = "Update successfully.")
  @UnauthorizedApiResponse
  @BadRequestApiResponse
  @NotFoundApiResponse
  public ResponseEntity<ActionResponseDto> updateFile(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID fileId,
      @Valid @RequestBody UpdateFileRequestDto request) {
    return ResponseEntity.ok(fileService.updateFile(userId, fileId, request));
  }

  @PatchMapping("/api/files/{fileId}/move")
  @Operation(summary = "Move the selected file to a new folder.")
  @UnauthorizedApiResponse
  @BadRequestApiResponse
  @NotFoundApiResponse
  public ResponseEntity<ActionResponseDto> moveFile(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID fileId,
      @RequestParam UUID newFolderId) {
    return ResponseEntity.ok(fileService.moveFile(userId, fileId, newFolderId));
  }

  @DeleteMapping("/api/files/{fileId}")
  @Operation(summary = "Delete a file.")
  @ApiResponse(responseCode = "200", description = "Delete successfully.")
  @UnauthorizedApiResponse
  @NotFoundApiResponse
  public ResponseEntity<ActionResponseDto> deleteFile(
      @AuthenticationPrincipal UUID userId, @PathVariable UUID fileId) {
    return ResponseEntity.ok(fileService.deleteFile(userId, fileId));
  }

  @PatchMapping("/api/files/{fileId}/recover")
  @Operation(summary = "Recover a file.")
  @ApiResponse(responseCode = "200", description = "Recover successfully.")
  @UnauthorizedApiResponse
  @NotFoundApiResponse
  public ResponseEntity<ActionResponseDto> recoverFile(
      @AuthenticationPrincipal UUID userId, @PathVariable UUID fileId) {
    return ResponseEntity.ok(fileService.recoverFile(userId, fileId));
  }

  @DeleteMapping("/api/files/{fileId}/permanent")
  @Operation(summary = "Permanently delete a file.")
  @ApiResponse(responseCode = "200", description = "Delete successfully.")
  @UnauthorizedApiResponse
  @NotFoundApiResponse
  public ResponseEntity<ActionResponseDto> permanentlyDeleteFile(
      @AuthenticationPrincipal UUID userId, @PathVariable UUID fileId) {
    return ResponseEntity.ok(fileService.permanentlyDeleteFile(userId, fileId));
  }
}
