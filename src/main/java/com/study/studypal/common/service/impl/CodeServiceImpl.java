package com.study.studypal.common.service.impl;

import com.study.studypal.auth.enums.VerificationType;
import com.study.studypal.common.cache.CacheNames;
import com.study.studypal.common.service.CodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CodeServiceImpl implements CodeService {
    private static final int LENGTH = 6;
    private final CacheManager cacheManager;
    private final Cache cache = cacheManager.getCache(CacheNames.VERIFICATION_CODES);

    @Override
    public String generateVerificationCode(String email, VerificationType type) {
        String code = generateRandomCode();
        String key = generateKey(email, type);

        cache.put(key, code);

        return code;
    }

    @Override
    public boolean verifyCode(String email, String code, VerificationType type) {
        String key = generateKey(email, type);
        String storedCode = cache.get(key, String.class);

        if(storedCode == null || !storedCode.equals(code)) {
            return false;
        }

        cache.evict(key);
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
