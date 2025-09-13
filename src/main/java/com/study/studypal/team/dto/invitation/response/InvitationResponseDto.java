package com.study.studypal.team.dto.invitation.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvitationResponseDto {
    private UUID id;

    private UUID inviteeId;

    private String inviterName;

    private String inviterAvatarUrl;

    private UUID teamId;

    private String teamName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime invitedAt;
}
