package com.study.studypal.notification.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NotificationConstant {
  public static final String FCM_DATA_KEY_TYPE = "type";
  public static final String FCM_DATA_KEY_ID = "id";

  public static final String DATA_KEY_SUBJECT = "{subject}";
  public static final String DATA_KEY_RESOURCE = "{resource}";
  public static final String DATA_KEY_TIME = "{time}";
  public static final String DATA_KEY_DATE = "{date}";

  public static final String DATE_FORMAT = "dd-MM-yyyy";
  public static final String TIME_FORMAT = "HH:mm:ss";
}
