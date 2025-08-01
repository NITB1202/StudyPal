package com.study.studypal.dtos.Shared;

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
