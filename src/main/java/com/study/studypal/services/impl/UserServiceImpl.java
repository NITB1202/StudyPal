package com.study.studypal.services.impl;

import com.study.studypal.entities.User;
import com.study.studypal.enums.Gender;
import com.study.studypal.repositories.UserRepository;
import com.study.studypal.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UUID createDefaultProfile(String name) {
        User user = User.builder()
                .name(name)
                .dateOfBirth(LocalDate.now())
                .gender(Gender.UNSPECIFIED)
                .avatarUrl("")
                .build();

        userRepository.save(user);

        return user.getId();
    }

    @Override
    public UUID createProfile(String name, String avatarUrl) {
        User user = User.builder()
                .name(name)
                .dateOfBirth(LocalDate.now())
                .gender(Gender.UNSPECIFIED)
                .avatarUrl(avatarUrl)
                .build();

        userRepository.save(user);

        return user.getId();
    }
}
