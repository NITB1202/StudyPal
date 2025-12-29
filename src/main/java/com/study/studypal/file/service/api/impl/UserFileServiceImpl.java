package com.study.studypal.file.service.api.impl;

import static com.study.studypal.file.constant.FileConstant.FILE_FOLDER;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.dto.FileResponse;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.exception.code.FileErrorCode;
import com.study.studypal.common.service.FileService;
import com.study.studypal.common.util.FileUtils;
import com.study.studypal.file.dto.file.request.UpdateFileRequestDto;
import com.study.studypal.file.dto.file.response.DeletedFileResponseDto;
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
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
  private final TeamMembershipInternalService memberService;
  private final ModelMapper modelMapper;
  @PersistenceContext private final EntityManager entityManager;

  @Override
  @Transactional
  public FileResponseDto uploadFile(UUID userId, UUID folderId, String name, MultipartFile file) {
    try {
      Folder folder = folderService.getById(folderId);

      validationService.validateFile(file);
      validationService.validateViewFolderPermission(userId, folder);
      usageService.validateUsage(folder, file.getSize());

      String fileName = StringUtils.isNotBlank(name) ? name : FileUtils.extractFileName(file);
      String extension = FileUtils.extractFileExtension(file);

      validationService.validateFileName(folderId, fileName, extension);

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
      usageService.updateUsage(folder, file.getSize());

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
    Folder folder = folderService.getById(folderId);
    validationService.validateViewFolderPermission(userId, folder);

    Pageable pageable = PageRequest.of(0, size);
    List<File> files =
        cursor == null
            ? fileRepository.getFiles(folderId, pageable)
            : fileRepository.getFilesWithCursor(folderId, cursor, pageable);

    List<FileResponseDto> filesDTO =
        modelMapper.map(files, new TypeToken<List<FileResponseDto>>() {}.getType());

    long total = fileRepository.countFiles(folderId);

    LocalDateTime nextCursor =
        files.size() == size ? files.get(files.size() - 1).getUpdatedAt() : null;

    return ListFileResponseDto.builder()
        .files(filesDTO)
        .total(total)
        .nextCursor(nextCursor)
        .build();
  }

  @Override
  public ListDeletedFileResponseDto getDeletedFiles(
      UUID userId, UUID teamId, LocalDateTime cursor, int size) {
    Pageable pageable = PageRequest.of(0, size);

    if (teamId != null) {
      memberService.validateUserBelongsToTeam(userId, teamId);
    }

    List<File> files =
        teamId != null
            ? getTeamDeletedFiles(teamId, cursor, pageable)
            : getPersonalDeletedFiles(userId, cursor, pageable);

    List<DeletedFileResponseDto> filesDTO =
        modelMapper.map(files, new TypeToken<List<DeletedFileResponseDto>>() {}.getType());

    long total =
        teamId != null
            ? fileRepository.countTeamDeletedFiles(teamId)
            : fileRepository.countPersonalDeletedFiles(userId);

    LocalDateTime nextCursor =
        files.size() == size ? files.get(files.size() - 1).getUpdatedAt() : null;

    return ListDeletedFileResponseDto.builder()
        .files(filesDTO)
        .total(total)
        .nextCursor(nextCursor)
        .build();
  }

  @Override
  public ListFileResponseDto searchFilesByName(
      UUID userId, UUID folderId, String keyword, LocalDateTime cursor, int size) {
    Folder folder = folderService.getById(folderId);
    validationService.validateViewFolderPermission(userId, folder);

    Pageable pageable = PageRequest.of(0, size);
    String handledKeyword = keyword.toLowerCase().trim();

    List<File> files =
        cursor == null
            ? fileRepository.searchFilesByName(folderId, handledKeyword, pageable)
            : fileRepository.searchFilesByNameWithCursor(
                folderId, handledKeyword, cursor, pageable);

    List<FileResponseDto> filesDTO =
        modelMapper.map(files, new TypeToken<List<FileResponseDto>>() {}.getType());

    long total = fileRepository.countByName(folderId, handledKeyword);

    LocalDateTime nextCursor =
        files.size() == size ? files.get(files.size() - 1).getUpdatedAt() : null;

    return ListFileResponseDto.builder()
        .files(filesDTO)
        .total(total)
        .nextCursor(nextCursor)
        .build();
  }

  @Override
  @Transactional
  public ActionResponseDto updateFile(UUID userId, UUID fileId, UpdateFileRequestDto request) {
    File file =
        fileRepository
            .findByIdForUpdate(fileId)
            .orElseThrow(() -> new BaseException(UserFileErrorCode.FILE_NOT_FOUND));

    validationService.validateFileNotDeleted(file);
    validationService.validateUpdateFilePermission(userId, file);

    String name = request.getName();
    if (StringUtils.isNotBlank(name) && !name.equals(file.getName())) {
      UUID folderId = file.getFolder().getId();
      validationService.validateFileName(folderId, name, file.getExtension());

      file.setName(name);
      updateAuditFields(userId, file);

      fileRepository.save(file);
    }

    return ActionResponseDto.builder().success(true).message("Update successfully.").build();
  }

  @Override
  @Transactional
  public ActionResponseDto moveFile(UUID userId, UUID fileId, UUID newFolderId) {
    File file =
        fileRepository
            .findByIdForUpdate(fileId)
            .orElseThrow(() -> new BaseException(UserFileErrorCode.FILE_NOT_FOUND));

    validationService.validateFileNotDeleted(file);
    validationService.validateUpdateFilePermission(userId, file);

    Folder currentFolder = file.getFolder();
    UUID currentFolderId = currentFolder.getId();

    if (!currentFolderId.equals(newFolderId)) {
      Folder newFolder = folderService.getById(newFolderId);

      if (!Objects.equals(currentFolder.getTeam(), newFolder.getTeam())) {
        throw new BaseException(UserFileErrorCode.PERMISSION_MOVE_FILE_DENIED);
      }

      usageService.validateUsage(newFolder, file.getBytes());

      file.setFolder(newFolder);
      updateAuditFields(userId, file);

      folderService.decreaseFile(userId, currentFolder, file);
      folderService.increaseFile(userId, newFolder, file);

      fileRepository.save(file);
    }

    return ActionResponseDto.builder().success(true).message("Move successfully.").build();
  }

  @Override
  public ActionResponseDto deleteFile(UUID userId, UUID fileId) {
    return null;
  }

  @Override
  public ActionResponseDto restoreFile(UUID userId, UUID fileId) {
    return null;
  }

  private List<File> getTeamDeletedFiles(UUID teamId, LocalDateTime cursor, Pageable pageable) {
    return cursor == null
        ? fileRepository.getTeamDeletedFiles(teamId, pageable)
        : fileRepository.getTeamDeletedFilesWithCursor(teamId, cursor, pageable);
  }

  private List<File> getPersonalDeletedFiles(UUID userId, LocalDateTime cursor, Pageable pageable) {
    return cursor == null
        ? fileRepository.getPersonalDeletedFiles(userId, pageable)
        : fileRepository.getPersonalDeletedFilesWithCursor(userId, cursor, pageable);
  }

  private void updateAuditFields(UUID userId, File file) {
    User user = entityManager.getReference(User.class, userId);
    file.setUpdatedBy(user);
    file.setUpdatedAt(LocalDateTime.now());
  }
}
