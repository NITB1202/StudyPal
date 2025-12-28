package com.study.studypal.file.repository;

import com.study.studypal.file.entity.Folder;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FolderRepository extends JpaRepository<Folder, UUID> {
  boolean existsByNameAndCreatedBy_IdAndTeamIdIsNull(String name, UUID createdBy);

  boolean existsByNameAndTeamId(String name, UUID teamId);

  @Query(
      """
    SELECT f
    FROM Folder f
    WHERE f.createdBy.id = :userId
    AND f.team IS NULL
    AND f.isDeleted = false
    ORDER BY f.updatedAt DESC
    """)
  List<Folder> getPersonalFolders(@Param("userId") UUID userId, Pageable pageable);

  @Query(
      """
    SELECT f
    FROM Folder f
    WHERE f.createdBy.id = :userId
    AND f.team IS NULL
    AND f.isDeleted = false
    AND f.updatedAt <= :cursor
    ORDER BY f.updatedAt DESC
    """)
  List<Folder> getPersonalFoldersWithCursor(
      @Param("userId") UUID userId, @Param("cursor") LocalDateTime cursor, Pageable pageable);

  @Query(
      """
    SELECT f
    FROM Folder f
    WHERE f.team.id = :teamId
    AND f.isDeleted = false
    ORDER BY f.updatedAt DESC
    """)
  List<Folder> getTeamFolders(@Param("teamId") UUID teamId, Pageable pageable);

  @Query(
      """
    SELECT f
    FROM Folder f
    WHERE f.team.id = :teamId
    AND f.isDeleted = false
    AND f.updatedAt <= :cursor
    ORDER BY f.updatedAt DESC
    """)
  List<Folder> getTeamFoldersWithCursor(
      @Param("teamId") UUID teamId, @Param("cursor") LocalDateTime cursor, Pageable pageable);

  @Query(
      """
    SELECT COUNT(f)
    FROM Folder f
    WHERE f.team.id = :teamId
    AND f.isDeleted = false
    """)
  long countTeamFolders(@Param("teamId") UUID teamId);

  @Query(
      """
    SELECT COUNT(f)
    FROM Folder f
    WHERE f.createdBy.id = :userId
    AND f.team IS NULL
    AND f.isDeleted = false
    """)
  long countPersonalFolders(@Param("userId") UUID userId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT f FROM Folder f WHERE f.id = :folderId")
  Optional<Folder> findByIdForUpdate(@Param("folderId") UUID folderId);
}
