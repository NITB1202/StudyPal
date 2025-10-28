package com.study.studypal.plan.service.internal;

import com.study.studypal.plan.dto.plancomment.response.PlanCommentResponseDto;
import java.util.List;
import java.util.UUID;

public interface PlanCommentInternalService {
  List<PlanCommentResponseDto> getAll(UUID planId);
}
