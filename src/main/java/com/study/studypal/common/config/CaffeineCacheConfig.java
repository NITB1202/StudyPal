package com.study.studypal.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.study.studypal.common.cache.CacheSpec;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.exception.errorCode.ConfigErrorCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableCaching
@ConfigurationProperties(prefix = "app.caches")
@Getter
@Setter
public class CaffeineCacheConfig {
    private Map<String, CacheSpec> cacheSpecs = new HashMap<>();

    @Bean
    public CacheManager cacheManager() {
        if (cacheSpecs == null || cacheSpecs.isEmpty()) {
            throw new BaseException(ConfigErrorCode.MISSING_CACHE_CONFIG);
        }

        List<CaffeineCache> caches = cacheSpecs.entrySet()
                .stream()
                .map(entry -> {
                    CacheSpec spec = entry.getValue();
                    return new CaffeineCache(entry.getKey(),
                            Caffeine.newBuilder()
                                    .expireAfterWrite(spec.getTtl())
                                    .maximumSize(spec.getMaxSize())
                                    .build());
                })
                .toList();

        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(caches);
        return cacheManager;
    }
}
