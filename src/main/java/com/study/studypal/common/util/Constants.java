package com.study.studypal.common.util;

import java.time.format.DateTimeFormatter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {
  // API constants
  public static final String HEADER_IDEMPOTENCY_KEY = "Idempotency-Key";
  public static final String DEFAULT_PAGE_SIZE = "10";

  // Authentication constants
  public static final String PASSWORD_RULE_MESSAGE =
      "Password must be at least 3 characters long and contain both letters and numbers.";
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

  // System image folder
  public static final String USER_AVATAR_FOLDER = "users";
  public static final String TEAM_AVATAR_FOLDER = "teams";

  // Team constants
  public static final long MAX_OWNED_TEAMS = 5;

  // Plan constants
  public static final String PLAN_CODE_PREFIX = "PLN-";
  public static final String TASK_CODE_PREFIX = "TSK-";
  public static final String CODE_NUMBER_FORMAT = "%05d";
  public static final DateTimeFormatter JSON_DATETIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
}
