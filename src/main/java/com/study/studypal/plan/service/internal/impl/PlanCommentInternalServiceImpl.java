package com.study.studypal.plan.service.internal.impl;

import com.study.studypal.plan.dto.plancomment.response.PlanCommentResponseDto;
import com.study.studypal.plan.entity.PlanComment;
import com.study.studypal.plan.repository.PlanCommentRepository;
import com.study.studypal.plan.service.internal.PlanCommentInternalService;
import com.study.studypal.user.entity.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanCommentInternalServiceImpl implements PlanCommentInternalService {
  private final PlanCommentRepository planCommentRepository;
  private final ModelMapper modelMapper;

  @Override
  public List<PlanCommentResponseDto> getAll(UUID planId) {
    List<PlanComment> comments = planCommentRepository.findAllByPlanIdOrderByCreatedAtDesc(planId);

    Map<UUID, Integer> indexMap = new HashMap<>();
    List<PlanCommentResponseDto> responseDtoList = new ArrayList<>();

    for (PlanComment comment : comments) {
      PlanCommentResponseDto responseDto = toPlanCommentResponseDto(comment);
      PlanComment parentComment = comment.getParentComment();

      if (parentComment == null) {
        responseDtoList.add(responseDto);
        indexMap.put(responseDto.getId(), responseDtoList.size() - 1);
      } else {
        int parentIndex = indexMap.get(parentComment.getId());
        responseDtoList.get(parentIndex).getReplies().add(responseDto);
      }
    }

    return responseDtoList;
  }

  private PlanCommentResponseDto toPlanCommentResponseDto(PlanComment comment) {
    User user = comment.getUser();
    PlanCommentResponseDto responseDto = modelMapper.map(comment, PlanCommentResponseDto.class);
    responseDto.setUserId(user.getId());
    responseDto.setAvatarUrl(user.getAvatarUrl());
    return responseDto;
  }
}
