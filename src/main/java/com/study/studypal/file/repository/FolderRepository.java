package com.study.studypal.file.repository;

import com.study.studypal.file.entity.Folder;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FolderRepository extends JpaRepository<Folder, UUID> {}
