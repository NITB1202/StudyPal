package com.study.studypal.plan.repository;

import com.study.studypal.plan.entity.PlanComment;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanCommentRepository extends JpaRepository<PlanComment, UUID> {
  List<PlanComment> findAllByPlanIdOrderByCreatedAtDesc(UUID planId);
}
