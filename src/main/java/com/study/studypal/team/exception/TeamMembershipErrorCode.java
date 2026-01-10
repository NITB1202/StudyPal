package com.study.studypal.team.exception;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TeamMembershipErrorCode implements ErrorCode {
  USER_MEMBERSHIP_NOT_FOUND(
      HttpStatus.NOT_FOUND, "TEAM_MEM_001", "You are not a member of this team."),
  TARGET_MEMBERSHIP_NOT_FOUND(
      HttpStatus.NOT_FOUND, "TEAM_MEM_002", "The user with id %s is not part of this team."),
  PERMISSION_UPDATE_TEAM_DENIED(
      HttpStatus.FORBIDDEN, "TEAM_MEM_003", "Only the owner can update the team."),
  PERMISSION_UPDATE_MEMBER_ROLE_DENIED(
      HttpStatus.FORBIDDEN, "TEAM_MEM_004", "Only the owner can update another member's role."),
  PERMISSION_INVITE_MEMBER_DENIED(
      HttpStatus.FORBIDDEN,
      "TEAM_MEM_005",
      "Only the owner or an admin can invite members to this team."),
  PERMISSION_REMOVE_MEMBER_RESTRICTED(
      HttpStatus.FORBIDDEN,
      "TEAM_MEM_006",
      "You cannot remove members with an equal or higher role than yours."),
  CANNOT_UPDATE_OWN_ROLE(
      HttpStatus.BAD_REQUEST, "TEAM_MEM_007", "You cannot update your own role."),
  CANNOT_REMOVE_SELF(
      HttpStatus.BAD_REQUEST, "TEAM_MEM_008", "You cannot remove yourself from the team."),
  CANNOT_LEAVE_AS_CREATOR(
      HttpStatus.BAD_REQUEST,
      "TEAM_MEM_009",
      "You are the owner of the team. Please transfer ownership before leaving."),
  USER_ALREADY_IN_TEAM(
      HttpStatus.CONFLICT, "TEAM_MEM_010", "You are already a member of this team."),
  INVITEE_ALREADY_IN_TEAM(
      HttpStatus.CONFLICT, "TEAM_MEM_011", "The invitee is already a member of this team."),
  MEMBER_ALREADY_REMOVED(
      HttpStatus.CONFLICT, "TEAM_MEM_012", "The member has already been removed from the team."),
  TEAM_OWNER_LIMIT_REACHED(
      HttpStatus.BAD_REQUEST,
      "TEAM_MEM_013",
      "The selected member already owns the maximum number of teams allowed."),
  PERMISSION_UPDATE_PLAN_DENIED(
      HttpStatus.FORBIDDEN, "TEAM_MEM_014", "Only the owner or an admin can update team's plan."),
  CANNOT_LEAVE_WITH_REMAINING_TASKS(
      HttpStatus.FORBIDDEN,
      "TEAM_MEM_015",
      "You must complete or reassign your tasks before leaving the team."),
  CANNOT_REMOVE_MEMBER_WITH_REMAINING_TASKS(
      HttpStatus.FORBIDDEN, "TEAM_MEM_016", "Cannot remove member who still has assigned tasks."),
  PERMISSION_UPDATE_FOLDER_DENIED(
      HttpStatus.FORBIDDEN, "TEAM_MEM_017", "Only the owner or an admin can update team's folder."),
  ;

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  TeamMembershipErrorCode(final HttpStatus httpStatus, final String code, final String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}
