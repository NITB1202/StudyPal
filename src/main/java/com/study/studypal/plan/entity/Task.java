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
@Table(name = "tasks")
public class Task {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "plan_id", nullable = false)
  private UUID planId;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "assignee_id", nullable = false)
  private UUID assigneeId;

  @Column(name = "due_date")
  private LocalDateTime dueDate;

  @Column(name = "complete_date", nullable = false)
  private LocalDateTime completeDate;
}
