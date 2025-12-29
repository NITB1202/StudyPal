package com.study.studypal.file.service.internal.impl;

import static com.study.studypal.file.constant.FileConstant.DEFAULT_FOLDER_NAME;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.file.entity.File;
import com.study.studypal.file.entity.Folder;
import com.study.studypal.file.exception.FolderErrorCode;
import com.study.studypal.file.repository.FolderRepository;
import com.study.studypal.file.service.internal.FolderInternalService;
import com.study.studypal.team.entity.Team;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FolderInternalServiceImpl implements FolderInternalService {
  private final FolderRepository folderRepository;
  @PersistenceContext private final EntityManager entityManager;

  @Override
  @Transactional
  public void createTeamDefaultFolder(UUID userId, UUID teamId) {
    User user = entityManager.getReference(User.class, userId);
    Team team = entityManager.getReference(Team.class, teamId);
    LocalDateTime now = LocalDateTime.now();

    Folder folder =
        Folder.builder()
            .name(DEFAULT_FOLDER_NAME)
            .createdBy(user)
            .createdAt(now)
            .updatedBy(user)
            .updatedAt(now)
            .bytes(0L)
            .fileCount(0)
            .isDeleted(false)
            .team(team)
            .build();

    folderRepository.save(folder);
  }

  @Override
  public Folder getById(UUID id) {
    return folderRepository
        .findById(id)
        .orElseThrow(() -> new BaseException(FolderErrorCode.FOLDER_NOT_FOUND));
  }

  @Override
  public void updateAuditFields(UUID userId, Folder folder) {
    User user = entityManager.getReference(User.class, userId);
    folder.setUpdatedBy(user);
    folder.setUpdatedAt(LocalDateTime.now());
  }

  @Override
  public void increaseFile(UUID userId, Folder folder, File file) {
    folder.setFileCount(folder.getFileCount() + 1);
    folder.setBytes(folder.getBytes() + file.getBytes());
    updateAuditFields(userId, folder);

    folderRepository.save(folder);
  }

  @Override
  public void decreaseFile(UUID userId, Folder folder, File file) {
    folder.setFileCount(folder.getFileCount() - 1);
    folder.setBytes(folder.getBytes() - file.getBytes());
    updateAuditFields(userId, folder);

    folderRepository.save(folder);
  }
}
