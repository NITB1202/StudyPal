package com.study.studypal.auth.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthConstant {
  public static final String PASSWORD_RULE_MESSAGE =
      "Password must be at least 3 characters long and contain both letters and numbers.";
  public static final int VERIFICATION_CODE_LENGTH = 6;
  public static final String VERIFICATION_EMAIL_SUBJECT = "Verify Your Email Address";
  public static final String VERIFICATION_EMAIL_CONTENT =
      "<div style='font-family: Arial, sans-serif; padding: 20px;'>"
          + "<h2>Email Verification</h2>"
          + "<p>Use the verification code below:</p>"
          + "<div style='font-size: 24px; font-weight: bold; color: #2F54EB; margin: 20px 0;'>"
          + "%s"
          + "</div>"
          + "<p>This code will expire in 5 minutes.</p>"
          + "<p style='margin-top: 40px;'>Regards,<br><strong>StudyPal</strong></p>"
          + "</div>";
}
