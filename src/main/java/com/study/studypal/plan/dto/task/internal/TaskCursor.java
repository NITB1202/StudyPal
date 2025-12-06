package com.study.studypal.plan.dto.task.internal;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskCursor(LocalDateTime dueDate, Integer priorityValue, UUID id) {}
