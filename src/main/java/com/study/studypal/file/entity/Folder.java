package com.study.studypal.file.entity;

import com.study.studypal.team.entity.Team;
import com.study.studypal.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
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
@Table(name = "folders")
public class Folder {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "name", nullable = false, length = 200)
  private String name;

  @ManyToOne
  @JoinColumn(name = "creator_id", nullable = false)
  private User creator;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "usage", nullable = false)
  private Long usage;

  @ManyToOne
  @JoinColumn(name = "team_id")
  private Team team;

  @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<File> files;

  @Column(name = "is_deleted", nullable = false)
  private Boolean isDeleted;
}
