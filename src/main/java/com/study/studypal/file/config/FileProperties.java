package com.study.studypal.file.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@Configuration
@ConfigurationProperties(prefix = "file")
@Getter
@Setter
public class FileProperties {
  private Long userUsageLimit;
  private Long teamUsageLimit;
  private DataSize maxSize;
}
