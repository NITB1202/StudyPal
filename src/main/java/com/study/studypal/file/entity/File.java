package com.study.studypal.file.entity;

import com.study.studypal.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "files")
public class File {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(nullable = false, length = 200)
  private String name;

  @Column(nullable = false, length = 20)
  private String extension;

  @ManyToOne
  @JoinColumn(name = "folder_id", nullable = false)
  private Folder folder;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by", nullable = false)
  private User createdBy;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "updated_by", nullable = false)
  private User updatedBy;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(name = "bytes", nullable = false)
  private Long bytes;

  @Column(name = "url", nullable = false, length = 200)
  private String url;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;
}
