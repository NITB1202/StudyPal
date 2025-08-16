package com.study.studypal.common.config;

import com.study.studypal.common.service.FileService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {
    @Bean
    public FileService fileService() {
        return mock(FileService.class);
    }
}
