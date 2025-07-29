package com.study.studypal.services;

public interface MailService {
    void sendVerificationEmail(String email, String verificationCode);
}
