package com.study.studypal.file.service.api;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.file.dto.file.request.UpdateFileRequestDto;
import com.study.studypal.file.dto.file.response.FileDetailResponseDto;
import com.study.studypal.file.dto.file.response.FileResponseDto;
import com.study.studypal.file.dto.file.response.ListDeletedFileResponseDto;
import com.study.studypal.file.dto.file.response.ListFileResponseDto;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface UserFileService {
  FileResponseDto uploadFile(UUID userId, UUID folderId, String name, MultipartFile file);

  FileDetailResponseDto getFileDetail(UUID userId, UUID fileId);

  ListFileResponseDto getFiles(UUID userId, UUID folderId, LocalDateTime cursor, int size);

  ListDeletedFileResponseDto getDeletedFiles(
      UUID userId, UUID teamId, LocalDateTime cursor, int size);

  ListFileResponseDto searchFilesByName(
      UUID userId, UUID folderId, String keyword, LocalDateTime cursor, int size);

  ActionResponseDto updateFile(UUID userId, UUID fileId, UpdateFileRequestDto request);

  ActionResponseDto moveFile(UUID userId, UUID fileId, UUID newFolderId);

  ActionResponseDto deleteFile(UUID userId, UUID fileId);

  ActionResponseDto recoverFile(UUID userId, UUID fileId);
}
