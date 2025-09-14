package com.study.studypal.common.cache;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CacheSpec {
  private Duration ttl;
  private long maxSize;
}
