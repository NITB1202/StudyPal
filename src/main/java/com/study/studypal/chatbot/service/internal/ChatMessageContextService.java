package com.study.studypal.chatbot.service.internal;

import com.study.studypal.chatbot.enums.ContextType;
import java.util.UUID;

public interface ChatMessageContextService {
  String getContextCode(UUID contextId, ContextType contextType);

  String validateAndSerializeContext(UUID userId, UUID contextId, ContextType contextType);
}
