package com.study.studypal.common.service;

public interface MailService {
  void sendVerificationEmail(String email, String verificationCode);
}
