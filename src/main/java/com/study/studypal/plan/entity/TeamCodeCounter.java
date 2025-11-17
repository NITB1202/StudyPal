package com.study.studypal.plan.entity;

import com.study.studypal.team.entity.Team;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "team_code_counters")
public class TeamCodeCounter {
  @Id
  @Column(name = "team_id", nullable = false)
  private UUID teamId;

  @MapsId
  @JoinColumn(name = "team_id")
  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Team team;

  @Column(name = "plan_counter", nullable = false)
  private Long planCounter;

  @Column(name = "task_counter", nullable = false)
  private Long taskCounter;
}
