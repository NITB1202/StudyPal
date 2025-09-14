package com.study.studypal.common.util;

import java.util.Arrays;
import java.util.stream.Collectors;

/** Utility class for generating consistent cache keys. */
public class CacheKeyUtils {
  private CacheKeyUtils() {}

  /**
   * Generate a cache key from any number of parts (null-safe).
   *
   * @param parts parts of the key
   * @return formatted cache key joined by ":"
   */
  public static String of(Object... parts) {
    return Arrays.stream(parts)
        .map(p -> p == null ? "null" : p.toString())
        .collect(Collectors.joining(":"));
  }
}
