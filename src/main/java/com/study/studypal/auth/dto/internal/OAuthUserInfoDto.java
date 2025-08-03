package com.study.studypal.auth.dto.internal;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OAuthUserInfoDto {
    private String id;

    private String name;

    private String email;

    private String picture;
}
