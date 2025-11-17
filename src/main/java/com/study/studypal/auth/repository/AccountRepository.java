package com.study.studypal.auth.repository;

import com.study.studypal.auth.entity.Account;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
  boolean existsByEmail(String email);

  Account findByEmail(String email);

  Optional<Account> findByUserId(UUID userId);
}
