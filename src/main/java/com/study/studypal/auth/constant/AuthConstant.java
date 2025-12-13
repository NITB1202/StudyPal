package com.study.studypal.auth.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthConstant {
  public static final String PASSWORD_RULE_MESSAGE =
      "Password must be at least 3 characters long and contain both letters and numbers.";
  public static final int VERIFICATION_CODE_LENGTH = 6;
  public static final String VERIFICATION_EMAIL_SUBJECT = "Verify Your Email Address";
  public static final String VERIFICATION_EMAIL_TEMPLATE_PATH = "verification_email";
  public static final String VERIFICATION_EMAIL_CODE_VARIABLE = "code";
}
