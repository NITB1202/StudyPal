package com.study.studypal.chat.job;

import com.study.studypal.chat.config.ChatProperties;
import com.study.studypal.chat.entity.Message;
import com.study.studypal.chat.service.internal.MessageAttachmentService;
import com.study.studypal.chat.service.internal.MessageService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageCleanUpJob implements Job {
  private final ChatProperties properties;
  private final MessageService messageService;
  private final MessageAttachmentService attachmentService;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime cutoffTime = now.minusDays(properties.getMessageCutoffDays());

    List<Message> messages = messageService.getMessagesBefore(cutoffTime);
    for (Message message : messages) {
      attachmentService.deleteAttachmentsByMessageId(message.getId());
    }
    messageService.hardDeleteMessages(messages);
  }
}
