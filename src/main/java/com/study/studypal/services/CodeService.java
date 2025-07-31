package com.study.studypal.services;

import com.study.studypal.enums.VerificationType;

public interface CodeService {
    String generateVerificationCode(String email, VerificationType type);
    boolean verifyCode(String email, String code, VerificationType type);
    String generateTeamCode();
}
