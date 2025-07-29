package com.study.studypal.services;

import com.study.studypal.enums.VerificationType;

public interface CodeService {
    String generateCode(String email, VerificationType type);
    boolean verifyCode(String email, String code, VerificationType type);
}
