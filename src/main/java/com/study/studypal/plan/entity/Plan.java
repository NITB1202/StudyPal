package com.study.studypal.plan.entity;

import com.study.studypal.plan.enums.Priority;
import com.study.studypal.team.entity.Team;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

  @Enumerated(EnumType.STRING)
  @Column(name = "priority", nullable = false)
  private Priority priority;

  @Column(name = "progress", nullable = false)
  private Float progress;

  @Column(name = "complete_date")
  private LocalDateTime completeDate;

  @Column(name = "is_deleted", nullable = false)
  private Boolean isDeleted;

  // For team plan
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team_id")
  private Team team;

  // For repeated plan
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_plan_id")
  private Plan parentPlan;
}
