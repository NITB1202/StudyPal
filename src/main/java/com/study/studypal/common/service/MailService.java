package com.study.studypal.common.service;

public interface MailService {
  void sendHtmlEmail(String email, String subject, String htmlContent);
}
