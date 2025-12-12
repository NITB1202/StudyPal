package com.study.studypal.notification.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "notification")
@Getter
@Setter
public class NotificationProperties {
  private int deviceTokenCutoffDays;
  private int notificationCutoffDays;
}
