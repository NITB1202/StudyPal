package com.study.studypal.services.impl;

import com.study.studypal.dtos.Shared.ActionResponseDto;
import com.study.studypal.dtos.User.request.UpdateUserRequestDto;
import com.study.studypal.dtos.User.response.ListUserResponseDto;
import com.study.studypal.dtos.User.response.UserDetailResponseDto;
import com.study.studypal.dtos.User.response.UserSummaryResponseDto;
import com.study.studypal.entities.User;
import com.study.studypal.enums.Gender;
import com.study.studypal.exceptions.BusinessException;
import com.study.studypal.exceptions.NotFoundException;
import com.study.studypal.repositories.UserRepository;
import com.study.studypal.services.FileService;
import com.study.studypal.services.UserService;
import com.study.studypal.utils.FileUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final FileService fileService;
    private static final String AVATAR_FOLDER = "users";

    @Override
    public UUID createDefaultProfile(String name) {
        User user = User.builder()
                .name(name)
                .dateOfBirth(LocalDate.now())
                .gender(Gender.UNSPECIFIED)
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

    @Override
    public UserSummaryResponseDto getUserSummaryProfile(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new NotFoundException("User with id " + userId + " not found.")
        );

        return modelMapper.map(user, UserSummaryResponseDto.class);
    }

    @Override
    public UserDetailResponseDto getUserProfile(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new NotFoundException("User with id " + userId + " not found.")
        );

        return modelMapper.map(user, UserDetailResponseDto.class);
    }

    @Override
    public ListUserResponseDto searchUsersByName(UUID userId, String keyword, UUID cursor, int size) {
        String handledKeyword = keyword.toLowerCase().trim();
        Pageable pageable = PageRequest.of(0, size, Sort.by("id").ascending());

        List<User> users = userRepository.searchByNameWithCursor(userId, handledKeyword, cursor, pageable);
        List<UserSummaryResponseDto> summaries = modelMapper.map(users, new TypeToken<List<UserSummaryResponseDto>>() {}.getType());

        long total = userRepository.countByName(userId, handledKeyword);
        UUID nextCursor = !users.isEmpty() && users.size() == size ? users.get(users.size() - 1).getId() : null;

        return ListUserResponseDto.builder()
                .users(summaries)
                .total(total)
                .nextCursor(nextCursor)
                .build();
    }

    @Override
    public UserDetailResponseDto updateUser(UUID userId, UpdateUserRequestDto request) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id " + userId + " not found.")
        );

        modelMapper.map(request, user);
        userRepository.save(user);

        return modelMapper.map(user, UserDetailResponseDto.class);
    }

    @Override
    public ActionResponseDto uploadUserAvatar(UUID userId, MultipartFile file) {
        if(!FileUtils.isImage(file)) {
            throw new BusinessException("User's avatar must be an image.");
        }

        try {
            String avatarUrl = fileService.uploadFile(AVATAR_FOLDER, userId.toString(), file.getBytes()).getUrl();
            User user = userRepository.findById(userId).orElseThrow(
                    () -> new NotFoundException("User with id " + userId + " not found.")
            );

            user.setAvatarUrl(avatarUrl);
            userRepository.save(user);

            return ActionResponseDto.builder()
                    .success(true)
                    .message("Uploaded avatar successfully.")
                    .build();

        } catch (IOException e) {
            throw new BusinessException("Reading file failed.");
        }
    }
}
