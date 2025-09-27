package com.study.studypal.plan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "plan_recurrence_rules")
public class PlanRecurrenceRule {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  private UUID id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "plan_id", nullable = false)
  private Plan plan;

  @Column(name = "week_days", nullable = false)
  private String weekDays; // Example: "MONDAY,WEDNESDAY,FRIDAY"

  @Column(name = "recurrence_start_date", nullable = false)
  private LocalDate recurrenceStartDate;

  @Column(name = "recurrence_end_date")
  private LocalDate recurrenceEndDate;

  @Column(name = "is_deleted", nullable = false)
  private Boolean isDeleted;
}
