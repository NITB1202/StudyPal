package com.study.studypal.common.service;

import com.study.studypal.auth.enums.VerificationType;

public interface CodeService {
  String generateVerificationCode(String email, VerificationType type);

  boolean verifyCode(String email, String code, VerificationType type);

  String generateTeamCode();
}
