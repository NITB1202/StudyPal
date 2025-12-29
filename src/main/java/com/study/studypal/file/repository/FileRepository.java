package com.study.studypal.file.repository;

import com.study.studypal.file.entity.File;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {
  List<File> findAllByFolderIdAndDeletedAtIsNull(UUID folderId);
}
