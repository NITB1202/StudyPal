package com.study.studypal.chat.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@Configuration
@ConfigurationProperties(prefix = "chat")
@Getter
@Setter
public class ChatProperties {
  private DataSize maxFileSize;
  private DataSize maxTotalSize;
  private Long editTimeLimitSeconds;
}
