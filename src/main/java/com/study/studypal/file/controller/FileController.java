package com.study.studypal.file.controller;

import com.study.studypal.common.exception.annotation.BadRequestApiResponse;
import com.study.studypal.common.exception.annotation.NotFoundApiResponse;
import com.study.studypal.common.exception.annotation.UnauthorizedApiResponse;
import com.study.studypal.file.dto.file.response.FileDetailResponseDto;
import com.study.studypal.file.dto.file.response.FileResponseDto;
import com.study.studypal.file.service.api.UserFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
}
