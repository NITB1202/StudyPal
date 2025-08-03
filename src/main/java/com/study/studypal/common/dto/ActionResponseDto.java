package com.study.studypal.common.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActionResponseDto {
    private boolean success;

    private String message;
}
