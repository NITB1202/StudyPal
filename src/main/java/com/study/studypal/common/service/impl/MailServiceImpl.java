package com.study.studypal.common.service.impl;

import com.study.studypal.common.exception.BusinessException;
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
    public void sendVerificationEmail(String email, String verificationCode) {
        String subject = "Verify Your Email Address";

        String htmlContent = "<div style='font-family: Arial, sans-serif; padding: 20px;'>"
                + "<h2>Email Verification</h2>"
                + "<p>Use the verification code below:</p>"
                + "<div style='font-size: 24px; font-weight: bold; color: #2F54EB; margin: 20px 0;'>"
                + verificationCode
                + "</div>"
                + "<p>This code will expire in 5 minutes.</p>"
                + "<p style='margin-top: 40px;'>Regards,<br><strong>StudyPal</strong></p>"
                + "</div>";

        sendHtmlMail(email, subject, htmlContent);
    }

    private void sendHtmlMail(String email, String subject, String htmlContent) {
        if(email == null || email.isEmpty()) {
            throw new BusinessException("This user hasn't provided an email yet.");
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new BusinessException("Failed to send email " + email);
        }
    }
}
