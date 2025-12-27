package com.study.studypal.file.service;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.file.dto.folder.request.CreateFolderRequestDto;
import com.study.studypal.file.dto.folder.response.FolderDetailResponseDto;
import com.study.studypal.file.dto.folder.response.FolderResponseDto;
import com.study.studypal.file.dto.folder.response.ListFolderResponseDto;
import java.time.LocalDateTime;
import java.util.UUID;

public interface FolderService {
  FolderResponseDto createFolder(UUID userId, UUID teamId, CreateFolderRequestDto request);

  FolderDetailResponseDto getFolderDetail(UUID userId, UUID folderId);

  ListFolderResponseDto getFolders(UUID userId, UUID teamId, LocalDateTime cursor, int size);

  ActionResponseDto updateFolderName(UUID userId, UUID folderId, String name);

  ActionResponseDto deleteFolder(UUID userId, UUID folderId);
}
