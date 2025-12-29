package com.study.studypal.file.repository;

import com.study.studypal.file.entity.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {
  List<File> findAllByFolderIdAndDeletedAtIsNull(UUID folderId);

  boolean existsByFolderIdAndNameAndExtension(UUID folderId, String name, String extension);

  @Query(
      """
    SELECT f
    FROM File f
    WHERE f.folder.id = :folderId
    AND f.deletedAt IS NULL
    ORDER BY f.updatedAt DESC
    """)
  List<File> getFiles(@Param("folderId") UUID folderId, Pageable pageable);

  @Query(
      """
    SELECT f
    FROM File f
    WHERE f.folder.id = :folderId
    AND f.deletedAt IS NULL
    AND f.updatedAt <= :cursor
    ORDER BY f.updatedAt DESC
    """)
  List<File> getFilesWithCursor(
      @Param("folderId") UUID folderId, @Param("cursor") LocalDateTime cursor, Pageable pageable);

  @Query(
      """
    SELECT COUNT(f)
    FROM File f
    WHERE f.folder.id = :folderId
    AND f.deletedAt IS NULL
    """)
  long countFiles(@Param("folderId") UUID folderId);

  @Query(
      """
    SELECT f
    FROM File f
    WHERE f.folder.id = :folderId
    AND f.deletedAt IS NULL
    AND LOWER(f.name) LIKE CONCAT('%', :keyword, '%')
    ORDER BY f.updatedAt DESC
    """)
  List<File> searchFilesByName(
      @Param("folderId") UUID folderId, @Param("keyword") String keyword, Pageable pageable);

  @Query(
      """
    SELECT f
    FROM File f
    WHERE f.folder.id = :folderId
    AND f.deletedAt IS NULL
    AND LOWER(f.name) LIKE CONCAT('%', :keyword, '%')
    AND f.updatedAt <= :cursor
    ORDER BY f.updatedAt DESC
    """)
  List<File> searchFilesByNameWithCursor(
      @Param("folderId") UUID folderId,
      @Param("keyword") String keyword,
      @Param("cursor") LocalDateTime cursor,
      Pageable pageable);

  @Query(
      """
    SELECT COUNT(f)
    FROM File f
    WHERE f.folder.id = :folderId
    AND f.deletedAt IS NULL
    AND LOWER(f.name) LIKE CONCAT('%', :keyword, '%')
    """)
  long countByName(@Param("folderId") UUID folderId, @Param("keyword") String keyword);

  @Query(
      """
    SELECT COUNT(f)
    FROM File f
    WHERE f.folder.team.id = :teamId
    AND f.deletedAt IS NOT NULL
    ORDER BY f.deletedAt DESC
    """)
  List<File> getTeamDeletedFiles(@Param("teamId") UUID teamId, Pageable pageable);

  @Query(
      """
    SELECT COUNT(f)
    FROM File f
    WHERE f.folder.team.id = :teamId
    AND f.deletedAt IS NOT NULL
    AND f.deletedAt <= :cursor
    ORDER BY f.deletedAt DESC
    """)
  List<File> getTeamDeletedFilesWithCursor(
      @Param("teamId") UUID teamId, @Param("cursor") LocalDateTime cursor, Pageable pageable);

  @Query(
      """
    SELECT f
    FROM File f
    WHERE f.createdBy.id = :userId
    AND f.folder.team IS NULL
    AND f.deletedAt IS NOT NULL
    ORDER BY f.deletedAt DESC
    """)
  List<File> getPersonalDeletedFiles(@Param("userId") UUID userId, Pageable pageable);

  @Query(
      """
    SELECT f
    FROM File f
    WHERE f.createdBy.id = :userId
    AND f.folder.team IS NULL
    AND f.deletedAt IS NOT NULL
    AND f.deletedAt <= :cursor
    ORDER BY f.deletedAt DESC
    """)
  List<File> getPersonalDeletedFilesWithCursor(
      @Param("userId") UUID userId, @Param("cursor") LocalDateTime cursor, Pageable pageable);

  @Query(
      """
    SELECT COUNT(f)
    FROM File f
    WHERE f.folder.team.id = :teamId
    AND f.deletedAt IS NOT NULL
    """)
  long countTeamDeletedFiles(@Param("teamId") UUID teamId);

  @Query(
      """
    SELECT COUNT(f)
    FROM File f
    WHERE f.createdBy.id = :userId
    AND f.folder.team IS NULL
    AND f.deletedAt IS NOT NULL
    """)
  long countPersonalDeletedFiles(@Param("userId") UUID userId);
}
