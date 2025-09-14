package com.study.studypal.user.service.internal.impl;

import com.study.studypal.user.entity.User;
import com.study.studypal.user.enums.Gender;
import com.study.studypal.user.repository.UserRepository;
import com.study.studypal.user.service.internal.UserInternalService;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserInternalServiceImpl implements UserInternalService {
  private final UserRepository userRepository;

  @Override
  public UUID createDefaultProfile(String name) {
    User user =
        User.builder().name(name).dateOfBirth(LocalDate.now()).gender(Gender.UNSPECIFIED).build();

    User savedUser = userRepository.save(user);

    return savedUser.getId();
  }

  @Override
  public UUID createProfile(String name, String avatarUrl) {
    User user =
        User.builder()
            .name(name)
            .dateOfBirth(LocalDate.now())
            .gender(Gender.UNSPECIFIED)
            .avatarUrl(avatarUrl)
            .build();

    User savedUser = userRepository.save(user);

    return savedUser.getId();
  }
}
