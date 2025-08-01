package com.study.studypal.auth.repository;

import com.study.studypal.auth.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    boolean existsByEmail(String email);
    Account findByEmail(String email);
}