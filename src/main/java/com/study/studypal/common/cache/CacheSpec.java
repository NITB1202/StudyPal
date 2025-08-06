package com.study.studypal.common.cache;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Getter
@Setter
public class CacheSpec {
    private Duration ttl;
    private long maxSize;
}