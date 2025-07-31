package com.study.studypal.repositories;

import com.study.studypal.entities.TeamUser;
import com.study.studypal.entities.TeamUserId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamUserRepository extends JpaRepository<TeamUser, TeamUserId> {
}