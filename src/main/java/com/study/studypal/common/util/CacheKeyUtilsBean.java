package com.study.studypal.common.util;

import org.springframework.stereotype.Component;

/**
 * Bean wrapper for CacheKeyUtils to allow short SpEL call in @Cacheable.
 * Usage: key = "@keys.of(#userId, #teamId)"
 */
@Component("keys")
public class CacheKeyUtilsBean {
    public String of(Object... parts) {
        return CacheKeyUtils.of(parts);
    }
}