package com.study.studypal.plan.dto.task.internal;

import java.util.UUID;

public record TaskStatisticsCursor(Long completedTaskCount, UUID userId) {}
