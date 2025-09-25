package com.study.studypal.plan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "plans")
public class Plan {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "creator_id", nullable = false)
  private UUID creatorId;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "note")
  private String note;

  @Column(name = "start_date", nullable = false)
  private LocalDateTime startDate;

  @Column(name = "due_date", nullable = false)
  private LocalDateTime dueDate;

  @Column(name = "progress", nullable = false)
  private Float progress;

  @Column(name = "complete_date")
  private LocalDateTime completeDate;

  @Column(name = "is_deleted", nullable = false)
  private Boolean isDeleted;

  @Column(name = "team_id")
  private UUID teamId;
}
