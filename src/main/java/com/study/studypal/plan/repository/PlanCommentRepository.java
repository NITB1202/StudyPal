package com.study.studypal.plan.repository;

import com.study.studypal.plan.entity.PlanComment;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanCommentRepository extends JpaRepository<PlanComment, UUID> {}
