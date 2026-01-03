package com.study.studypal.chat.service.internal;

import com.study.studypal.chat.entity.Message;
import com.study.studypal.chat.entity.MessageAttachment;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface MessageAttachmentService {
  List<MessageAttachment> saveAttachments(Message message, List<MultipartFile> files);
}
