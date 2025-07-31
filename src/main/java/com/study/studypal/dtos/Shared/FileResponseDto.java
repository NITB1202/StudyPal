package com.study.studypal.dtos.Shared;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileResponseDto {
    private String url;

    private long bytes;
}
