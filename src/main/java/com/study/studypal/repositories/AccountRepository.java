package com.study.studypal.repositories;

import com.study.studypal.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    boolean existsByEmail(String email);
    Account findByEmail(String email);
}