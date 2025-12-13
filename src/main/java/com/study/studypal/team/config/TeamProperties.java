package com.study.studypal.team.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "team")
@Getter
@Setter
public class TeamProperties {
  private int invitationCutoffDays;
  private int maxOwnedTeams;
}
