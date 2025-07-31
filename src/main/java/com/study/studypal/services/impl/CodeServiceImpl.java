package com.study.studypal.services.impl;

import com.study.studypal.enums.VerificationType;
import com.study.studypal.services.CodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class CodeServiceImpl implements CodeService {
    private final RedisTemplate<String, Object> redis;
    private static final int LENGTH = 6;
    private static final int MINUTES = 5;

    @Override
    public String generateVerificationCode(String email, VerificationType type) {
        String code = generateRandomCode();
        String key = generateKey(email, type);

        redis.opsForValue().set(key, code, Duration.ofMinutes(MINUTES));

        return code;
    }

    @Override
    public boolean verifyCode(String email, String code, VerificationType type) {
        String key = generateKey(email, type);
        String storedCode = (String) redis.opsForValue().get(key);

        if(storedCode == null || !storedCode.equals(code)) {
            return false;
        }

        redis.delete(key);
        return true;
    }

    @Override
    public String generateTeamCode() {
        return generateRandomCode();
    }

    private String generateKey(String email, VerificationType type) {
        return type + ":" + email.toLowerCase();
    }

    private String generateRandomCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < LENGTH; i++) {
            int rand = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(rand));
        }
        return sb.toString();
    }
}
