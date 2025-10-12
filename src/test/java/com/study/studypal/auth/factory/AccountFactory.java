package com.study.studypal.auth.factory;

import com.study.studypal.auth.entity.Account;
import com.study.studypal.auth.enums.AccountRole;
import com.study.studypal.auth.enums.AuthProvider;
import java.util.List;

public class AccountFactory {

  // ----------------- Entity -----------------

  public static Account createFullDetails() {
    return Account.builder()
        .providers(List.of(AuthProvider.LOCAL))
        .email("random@gmail.com")
        .role(AccountRole.USER)
        .build();
  }
}
