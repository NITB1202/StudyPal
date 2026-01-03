package com.study.studypal.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.exception.code.CommonErrorCode;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtils {
  private static final ObjectMapper objectMapper =
      new ObjectMapper()
          .findAndRegisterModules()
          .setSerializationInclusion(JsonInclude.Include.NON_NULL)
          .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

  public static Optional<String> trySerialize(Object object) {
    try {
      return Optional.of(objectMapper.writeValueAsString(object));
    } catch (JsonProcessingException ex) {
      log.error("Failed to serialize object: {}", object, ex);
      return Optional.empty();
    }
  }

  public static String serialize(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException ex) {
      log.error("Failed to serialize object: {}", object, ex);
      throw new BaseException(CommonErrorCode.JSON_SERIALIZE_FAILED);
    }
  }

  public static <T> T deserialize(String json, Class<T> clazz) {
    try {
      return objectMapper.readValue(json, clazz);
    } catch (JsonProcessingException ex) {
      log.error("Failed to deserialize JSON string: {}", json, ex);
      throw new BaseException(CommonErrorCode.JSON_DESERIALIZE_FAILED, ex);
    }
  }
}
