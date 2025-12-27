package com.study.studypal.chatbot.mapper;

import com.study.studypal.chatbot.dto.external.AIRequestDto;
import com.study.studypal.chatbot.dto.external.AIResponseDto;
import com.study.studypal.chatbot.dto.external.ExtractedFile;
import com.study.studypal.chatbot.dto.response.ChatResponseDto;
import com.study.studypal.chatbot.entity.ChatMessage;
import java.time.LocalDateTime;
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
    return AIRequestDto.builder().prompt(prompt).context(context).attachments(attachments).build();
  }

  public AIResponseDto toAIResponseDto(String reply, Long inputTokens, Long outputTokens) {
    return AIResponseDto.builder()
        .reply(reply)
        .inputTokens(inputTokens)
        .outputTokens(outputTokens)
        .build();
  }

  public ChatResponseDto toChatResponseDto(ChatMessage message) {
    ChatResponseDto responseDto = modelMapper.map(message, ChatResponseDto.class);
    responseDto.setReply(message.getMessage());
    return responseDto;
  }

  public ChatResponseDto toChatResponseDto(AIResponseDto aiResponse) {
    ChatResponseDto responseDto = modelMapper.map(aiResponse, ChatResponseDto.class);
    responseDto.setCreatedAt(LocalDateTime.now());
    return responseDto;
  }
}
