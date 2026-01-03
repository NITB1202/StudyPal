package com.study.studypal.chatbot.job;

import com.study.studypal.chatbot.config.ChatbotProperties;
import com.study.studypal.chatbot.entity.ChatMessage;
import com.study.studypal.chatbot.enums.Sender;
import com.study.studypal.chatbot.service.internal.ChatMessageAttachmentService;
import com.study.studypal.chatbot.service.internal.ChatMessageService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatbotMessageCleanUpJob implements Job {
  private final ChatbotProperties properties;
  private final ChatMessageService messageService;
  private final ChatMessageAttachmentService attachmentService;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime cutoffTime = now.minusDays(properties.getMessageCutoffDays());

    List<ChatMessage> messages = messageService.getMessagesBefore(cutoffTime);

    for (ChatMessage message : messages) {
      if (message.getSender().equals(Sender.AI)) {
        continue;
      }

      attachmentService.deleteAttachmentsByMessageId(message.getId());
    }

    messageService.deleteMessages(messages);
  }
}
