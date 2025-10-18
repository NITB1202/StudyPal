package com.study.studypal.plan.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LocalDateTimeListSerializer extends JsonSerializer<List<LocalDateTime>> {
  private static final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  @Override
  public void serialize(
      List<LocalDateTime> value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    gen.writeStartArray();
    for (LocalDateTime dateTime : value) {
      gen.writeString(dateTime.format(formatter));
    }
    gen.writeEndArray();
  }
}
