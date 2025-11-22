package com.study.studypal.common.service.impl;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.exception.code.MailErrorCode;
import com.study.studypal.common.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
  private final JavaMailSender mailSender;

  @Override
  public void sendHtmlEmail(String email, String subject, String htmlContent) {
    if (email == null || email.isEmpty()) {
      throw new BaseException(MailErrorCode.EMAIL_EMPTY);
    }

    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setTo(email);
      helper.setSubject(subject);
      helper.setText(htmlContent, true);

      mailSender.send(message);
    } catch (MessagingException e) {
      throw new BaseException(MailErrorCode.SEND_EMAIL_FAILED);
    }
  }
}
