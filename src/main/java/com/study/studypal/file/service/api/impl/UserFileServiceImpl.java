package com.study.studypal.file.service.api.impl;

import static com.study.studypal.file.constant.FileConstant.FILE_FOLDER;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.dto.FileResponse;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.exception.code.FileErrorCode;
import com.study.studypal.common.service.FileService;
import com.study.studypal.common.util.FileUtils;
import com.study.studypal.file.dto.file.request.UpdateFileRequestDto;
import com.study.studypal.file.dto.file.response.FileDetailResponseDto;
import com.study.studypal.file.dto.file.response.FileResponseDto;
import com.study.studypal.file.dto.file.response.ListDeletedFileResponseDto;
import com.study.studypal.file.dto.file.response.ListFileResponseDto;
import com.study.studypal.file.entity.File;
import com.study.studypal.file.entity.Folder;
import com.study.studypal.file.exception.UserFileErrorCode;
import com.study.studypal.file.repository.FileRepository;
import com.study.studypal.file.service.api.UserFileService;
import com.study.studypal.file.service.internal.FileValidationService;
import com.study.studypal.file.service.internal.FolderInternalService;
import com.study.studypal.file.service.internal.UsageService;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserFileServiceImpl implements UserFileService {
  private final FileRepository fileRepository;
  private final FileService fileService;
  private final FolderInternalService folderService;
  private final FileValidationService validationService;
  private final UsageService usageService;
  private final ModelMapper modelMapper;
  @PersistenceContext private final EntityManager entityManager;

  @Override
  @Transactional
  public FileResponseDto uploadFile(UUID userId, UUID folderId, String name, MultipartFile file) {
    try {
      Folder folder = folderService.getById(folderId);

      validationService.validateFile(file);
      validationService.validateViewFolderPermission(userId, folder);
      usageService.validateUsage(folder, file);

      String fileName = StringUtils.isNotBlank(name) ? name : FileUtils.extractFileName(file);
      String extension = FileUtils.extractFileExtension(file);

      if (fileRepository.existsByFolderIdAndNameAndExtension(folderId, fileName, extension)) {
        throw new BaseException(UserFileErrorCode.FILE_ALREADY_EXISTS);
      }

      UUID fileId = UUID.randomUUID();
      String systemFolderPath = String.format("%s/%s", FILE_FOLDER, folderId);
      FileResponse fileResponse =
          fileService.uploadFile(systemFolderPath, fileId.toString(), file.getBytes());

      LocalDateTime now = LocalDateTime.now();
      User user = entityManager.getReference(User.class, userId);

      File fileEntity =
          File.builder()
              .id(fileId)
              .name(fileName)
              .extension(extension)
              .folder(folder)
              .createdBy(user)
              .createdAt(now)
              .updatedBy(user)
              .updatedAt(now)
              .bytes(fileResponse.getBytes())
              .url(fileResponse.getUrl())
              .build();

      fileRepository.save(fileEntity);
      folderService.increaseFile(userId, folder, fileEntity);

      return modelMapper.map(fileEntity, FileResponseDto.class);
    } catch (IOException ex) {
      throw new BaseException(FileErrorCode.INVALID_FILE_CONTENT);
    }
  }

  @Override
  public FileDetailResponseDto getFileDetail(UUID userId, UUID fileId) {
    File file =
        fileRepository
            .findById(fileId)
            .orElseThrow(() -> new BaseException(UserFileErrorCode.FILE_NOT_FOUND));

    validationService.validateViewFolderPermission(userId, file.getFolder());

    FileDetailResponseDto response = modelMapper.map(file, FileDetailResponseDto.class);
    response.setCreatedBy(file.getCreatedBy().getName());
    response.setUpdatedBy(file.getUpdatedBy().getName());

    return response;
  }

  @Override
  public ListFileResponseDto getFiles(UUID userId, UUID folderId, LocalDateTime cursor, int size) {

    return null;
  }

  @Override
  public ListDeletedFileResponseDto getDeletedFiles(
      UUID userId, UUID teamId, LocalDateTime cursor, int size) {
    // membership if teamId != null

    return null;
  }

  @Override
  public ListFileResponseDto searchFilesByName(
      UUID userId, UUID folderId, String keyword, LocalDateTime cursor, int size) {
    return null;
  }

  @Override
  public FileResponseDto updateFile(UUID userId, UUID fileId, UpdateFileRequestDto request) {
    // admin < or owner
    return null;
  }

  @Override
  public ActionResponseDto moveFile(UUID userId, UUID fileId, UUID newFolderId) {
    return null;
  }

  @Override
  public ActionResponseDto deleteFile(UUID userId, UUID fileId) {
    return null;
  }

  @Override
  public ActionResponseDto restoreFile(UUID userId, UUID fileId) {
    return null;
  }
}
