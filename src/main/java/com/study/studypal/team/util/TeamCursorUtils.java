package com.study.studypal.team.util;

import com.study.studypal.common.util.CursorUtils;
import com.study.studypal.team.dto.membership.internal.DecodedCursor;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamCursorUtils {

  public static String encodeCursor(int rolePriority, String name, UUID userId) {
    return CursorUtils.encode(rolePriority, name, userId);
  }

  public static DecodedCursor decodeCursor(String encodedCursor) {
    List<String> parts = CursorUtils.decode(encodedCursor);
    return new DecodedCursor(
        Integer.parseInt(parts.get(0)), parts.get(1), UUID.fromString(parts.get(2)));
  }
}
