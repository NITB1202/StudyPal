package com.study.studypal.plan.entity;

import com.study.studypal.plan.enums.RecurrenceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
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
@Table(name = "task_recurrence_rules")
public class TaskRecurrenceRule {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  private UUID id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "task_id", unique = true, nullable = false)
  private Task task;

  @Column(name = "recurrence_start_date", nullable = false)
  private LocalDate recurrenceStartDate;

  @Column(name = "recurrence_end_date", nullable = false)
  private LocalDate recurrenceEndDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "recurrence_type", nullable = false)
  private RecurrenceType recurrenceType;

  @Column(name = "week_days")
  private String weekDays; // Example: "MONDAY,WEDNESDAY,FRIDAY"
}
