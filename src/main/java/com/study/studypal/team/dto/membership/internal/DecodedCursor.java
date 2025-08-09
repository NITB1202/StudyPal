package com.study.studypal.team.dto.membership.internal;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class DecodedCursor {
    private int rolePriority;

    private String name;

    private UUID userId;
}
