package com.study.studypal.plan.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "plan")
@Getter
@Setter
public class PlanProperties {
  private int taskCutoffDays;
}
