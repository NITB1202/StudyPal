package com.study.studypal.session.entity;

import com.study.studypal.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "session_settings")
public class SessionSetting {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  private UUID id;

  @MapsId
  @JoinColumn(name = "id")
  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User user;

  @Column(name = "focus_time_in_seconds", nullable = false)
  private Long focusTimeInSeconds;

  @Column(name = "break_time_in_seconds", nullable = false)
  private Long breakTimeInSeconds;

  @Column(name = "total_time_in_seconds", nullable = false)
  private Long totalTimeInSeconds;

  @Column(name = "enable_bg_music", nullable = false)
  private Boolean enableBgMusic;
}
