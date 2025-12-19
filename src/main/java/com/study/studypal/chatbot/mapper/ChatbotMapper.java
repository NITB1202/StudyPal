package com.study.studypal.chatbot.mapper;

import com.study.studypal.chatbot.dto.external.AIRequestDto;
import com.study.studypal.chatbot.dto.external.ExtractedFile;
import com.study.studypal.chatbot.dto.response.ChatResponseDto;
import com.study.studypal.chatbot.entity.ChatMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatbotMapper {
  private final ModelMapper modelMapper;

  public AIRequestDto toAIRequestDto(
      String prompt, String context, List<ExtractedFile> attachments) {
    AIRequestDto request = new AIRequestDto();
    request.setPrompt(prompt);
    request.setContext(context);
    request.setAttachments(attachments);
    return request;
  }

  public ChatResponseDto toChatResponseDto(ChatMessage message) {
    ChatResponseDto responseDto = modelMapper.map(message, ChatResponseDto.class);
    responseDto.setReply(message.getMessage());
    return responseDto;
  }
}
