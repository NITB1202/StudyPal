package com.study.studypal.auth.factory;

import com.study.studypal.auth.entity.Account;
import com.study.studypal.auth.enums.AccountRole;
import com.study.studypal.user.entity.User;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AccountFactory {

  // ----------------- Entity -----------------

  public static Account createWithUser(User user) {
    return Account.builder()
        .user(user)
        .providers(List.of())
        .email("acc_" + ThreadLocalRandom.current().nextInt(1000) + "@gmail.com")
        .role(AccountRole.USER)
        .build();
  }
}
