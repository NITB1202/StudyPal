package com.study.studypal.plan.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LocalDateTimeListDeserializer extends JsonDeserializer<List<LocalDateTime>> {
  private static final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  @Override
  public List<LocalDateTime> deserialize(JsonParser p, DeserializationContext ct)
      throws IOException {
    List<LocalDateTime> result = new ArrayList<>();
    JsonNode node = p.getCodec().readTree(p);
    for (JsonNode element : node) {
      result.add(LocalDateTime.parse(element.asText(), formatter));
    }
    return result;
  }
}
