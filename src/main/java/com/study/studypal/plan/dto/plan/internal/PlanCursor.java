package com.study.studypal.plan.dto.plan.internal;

import java.time.LocalDateTime;
import java.util.UUID;

public record PlanCursor(LocalDateTime dueDate, UUID id) {}
