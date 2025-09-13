package com.study.studypal.auth.dto.internal;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
