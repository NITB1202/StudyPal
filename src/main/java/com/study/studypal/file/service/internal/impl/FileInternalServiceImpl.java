package com.study.studypal.file.service.internal.impl;

import com.study.studypal.file.entity.File;
import com.study.studypal.file.repository.FileRepository;
import com.study.studypal.file.service.internal.FileInternalService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileInternalServiceImpl implements FileInternalService {
  private final FileRepository fileRepository;

  @Override
  public void softDeleteFilesInFolder(UUID folderId) {
    List<File> activeFiles = fileRepository.findAllByFolderIdAndDeletedAtIsNull(folderId);

    for (File activeFile : activeFiles) {
      activeFile.setDeletedAt(LocalDateTime.now());
    }

    fileRepository.saveAll(activeFiles);
  }

  @Override
  @Transactional
  public void hardDeleteFilesBefore(LocalDateTime cutoffTime) {
    fileRepository.deleteAllByDeletedAtBefore(cutoffTime);
  }
}
