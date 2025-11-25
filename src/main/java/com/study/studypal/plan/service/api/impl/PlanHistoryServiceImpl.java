package com.study.studypal.plan.service.api.impl;

import com.study.studypal.plan.dto.history.ListPlanHistoryResponseDto;
import com.study.studypal.plan.dto.history.PlanHistoryResponseDto;
import com.study.studypal.plan.entity.PlanHistory;
import com.study.studypal.plan.repository.PlanHistoryRepository;
import com.study.studypal.plan.service.api.PlanHistoryService;
import com.study.studypal.plan.service.internal.PlanInternalService;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanHistoryServiceImpl implements PlanHistoryService {
  private final PlanHistoryRepository planHistoryRepository;
  private final PlanInternalService planService;
  private final TeamMembershipInternalService memberService;
  private final ModelMapper modelMapper;

  @Override
  public ListPlanHistoryResponseDto getPlanHistory(
      UUID userId, UUID planId, LocalDateTime cursor, int size) {
    UUID teamId = planService.getTeamIdById(planId);
    memberService.validateUserBelongsToTeam(userId, teamId);

    Pageable pageable = PageRequest.of(0, size);

    List<PlanHistory> records =
        cursor == null
            ? planHistoryRepository.findByPlanIdOrderByTimestampDesc(planId, pageable)
            : planHistoryRepository.findByPlanIdWithCursor(planId, cursor, pageable);
    List<PlanHistoryResponseDto> recordsDTO =
        modelMapper.map(records, new TypeToken<List<PlanHistoryResponseDto>>() {}.getType());

    long total = planHistoryRepository.countByPlanId(planId);
    LocalDateTime nextCursor =
        !records.isEmpty() && records.size() == size
            ? records.get(records.size() - 1).getTimestamp()
            : null;

    return ListPlanHistoryResponseDto.builder()
        .records(recordsDTO)
        .total(total)
        .nextCursor(nextCursor)
        .build();
  }
}
