package com.study.studypal.file.entity;

import com.study.studypal.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

  @ManyToOne
  @JoinColumn(name = "uploaded_by", nullable = false)
  private User uploadedBy;

  @Column(name = "uploaded_at", nullable = false)
  private LocalDateTime uploadedAt;

  @Column(name = "url", nullable = false, length = 200)
  private String url;

  @Column(name = "size", nullable = false)
  private Long size;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;
}
