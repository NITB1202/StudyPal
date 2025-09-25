package com.study.studypal.user.service.internal.impl;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.user.dto.internal.UserSummaryProfile;
import com.study.studypal.user.entity.User;
import com.study.studypal.user.enums.Gender;
import com.study.studypal.user.exception.UserErrorCode;
import com.study.studypal.user.repository.UserRepository;
import com.study.studypal.user.service.internal.UserInternalService;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserInternalServiceImpl implements UserInternalService {
  private final UserRepository userRepository;
  private final ModelMapper modelMapper;

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

  @Override
  public UserSummaryProfile getUserSummaryProfile(UUID userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BaseException(UserErrorCode.USER_NOT_FOUND));

    return modelMapper.map(user, UserSummaryProfile.class);
  }
}
