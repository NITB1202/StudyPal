package com.study.studypal.common.cache;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CacheNames {
  public static final String REGISTER = "register";
  public static final String RESET_PASSWORD = "resetPassword";
  public static final String VERIFICATION_CODES = "verificationCodes";
  public static final String ACCESS_TOKENS = "accessTokens";
  public static final String REFRESH_TOKENS = "refreshTokens";
  public static final String USER_SUMMARY = "userSummary";
  public static final String USER_TEAMS = "userTeams";
  public static final String TEAM_OVERVIEW = "teamOverview";
  public static final String TEAM_MEMBERS = "teamMembers";
  public static final String INVITATIONS = "invitations";
  public static final String NOTIFICATIONS = "notifications";
}
