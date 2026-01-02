package com.study.studypal.session.entity;

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
import java.time.LocalTime;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User user;

  @Column(name = "focus_time", nullable = false)
  private LocalTime focusTime;

  @Column(name = "break_time", nullable = false)
  private LocalTime breakTime;

  @Column(name = "total_time", nullable = false)
  private LocalTime totalTime;

  @Column(name = "bg_music_name")
  private String bgMusicName;

  @Column(name = "bg_music_url")
  private String bgMusicUrl;
}
