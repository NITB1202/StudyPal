package com.study.studypal.session.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "session")
@Getter
@Setter
public class SessionProperties {
  private Long defaultFocusTimeInSeconds;
  private Long defaultBreakTimeInSeconds;
  private Long defaultTotalTimeInSeconds;
  private Boolean defaultEnableBgMusic;
  private Integer sessionCutoffDays;
}
