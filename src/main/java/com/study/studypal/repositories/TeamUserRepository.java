package com.study.studypal.repositories;

import com.study.studypal.entities.TeamUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TeamUserRepository extends JpaRepository<TeamUser, UUID> {
}