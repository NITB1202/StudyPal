package com.study.studypal.repositories;

import com.study.studypal.entities.Account;
import com.study.studypal.enums.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    boolean existsByProviderIdAndProvider(String providerId, AuthProvider provider);
    boolean existsByEmail(String email);
    Account findByProviderIdAndProvider(String providerId, AuthProvider provider);
    Account findByEmail(String email);
}