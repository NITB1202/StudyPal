package com.study.studypal.user.service.api.impl;

import com.study.studypal.common.cache.CacheNames;
import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.exception.file.FileProcessingException;
import com.study.studypal.common.exception.file.InvalidImageException;
import com.study.studypal.user.dto.request.UpdateUserRequestDto;
import com.study.studypal.user.dto.response.ListUserResponseDto;
import com.study.studypal.user.dto.response.UserDetailResponseDto;
import com.study.studypal.user.dto.response.UserSummaryResponseDto;
import com.study.studypal.user.entity.User;
import com.study.studypal.user.exception.UserErrorCode;
import com.study.studypal.user.repository.UserRepository;
import com.study.studypal.common.service.FileService;
import com.study.studypal.user.service.api.UserService;
import com.study.studypal.common.util.FileUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final FileService fileService;
    private static final String AVATAR_FOLDER = "users";

    @Override
    @Cacheable(value = CacheNames.USER_SUMMARY, key = "@keys.of(#userId)")
    public UserSummaryResponseDto getUserSummaryProfile(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BaseException(UserErrorCode.USER_NOT_FOUND)
        );

        return modelMapper.map(user, UserSummaryResponseDto.class);
    }

    @Override
    public UserDetailResponseDto getUserProfile(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BaseException(UserErrorCode.USER_NOT_FOUND)
        );

        return modelMapper.map(user, UserDetailResponseDto.class);
    }

    @Override
    public ListUserResponseDto searchUsersByName(UUID userId, String keyword, UUID cursor, int size) {
        String handledKeyword = keyword.toLowerCase().trim();
        Pageable pageable = PageRequest.of(0, size);

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
    @CacheEvict(value = CacheNames.USER_SUMMARY, key = "@keys.of(#userId)")
    public UserDetailResponseDto updateUser(UUID userId, UpdateUserRequestDto request) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BaseException(UserErrorCode.USER_NOT_FOUND)
        );

        modelMapper.map(request, user);
        userRepository.save(user);

        return modelMapper.map(user, UserDetailResponseDto.class);
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheNames.USER_SUMMARY, key = "@keys.of(#userId)")
    public ActionResponseDto uploadUserAvatar(UUID userId, MultipartFile file) {
        if(!FileUtils.isImage(file)) {
            throw new InvalidImageException();
        }

        try {
            String avatarUrl = fileService.uploadFile(AVATAR_FOLDER, userId.toString(), file.getBytes()).getUrl();
            User user = userRepository.findById(userId).orElseThrow(
                    () -> new BaseException(UserErrorCode.USER_NOT_FOUND)
            );

            user.setAvatarUrl(avatarUrl);
            userRepository.save(user);

            return ActionResponseDto.builder()
                    .success(true)
                    .message("Uploaded avatar successfully.")
                    .build();

        } catch (IOException e) {
            throw new FileProcessingException(e);
        }
    }
}
