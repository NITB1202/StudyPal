package com.study.studypal.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
@EnableCaching
@ConfigurationProperties(prefix = "app.caches")
@Getter
@Setter
public class CaffeineCacheConfig {
    private Map<String, CacheSpec> cacheSpecs;

    @Bean
    public CacheManager cacheManager() {
        List<CaffeineCache> caches = new ArrayList<>();
        for (Map.Entry<String, CacheSpec> entry : cacheSpecs.entrySet()) {
            CacheSpec spec = entry.getValue();
            Caffeine<Object, Object> builder = Caffeine.newBuilder()
                    .expireAfterWrite(spec.getTtl())
                    .maximumSize(spec.getMaxSize());
            caches.add(new CaffeineCache(entry.getKey(), builder.build()));
        }
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(caches);
        return cacheManager;
    }

    @Getter
    @Setter
    public static class CacheSpec {
        private Duration ttl;
        private long maxSize;
    }
}