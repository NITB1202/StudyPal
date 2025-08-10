package com.study.studypal.unit.user;

import com.study.studypal.user.entity.User;
import com.study.studypal.user.repository.UserRepository;
import com.study.studypal.user.service.internal.impl.UserInternalServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserInternalServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserInternalServiceImpl userInternalService;

    @Test
    void createDefaultProfile_shouldSaveUserAndReturnId() {
        // Arrange
        String name = "Test User";

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            return User.builder()
                    .id(UUID.randomUUID())
                    .name(u.getName())
                    .dateOfBirth(u.getDateOfBirth())
                    .gender(u.getGender())
                    .build();
        });

        // Act
        UUID resultId = userInternalService.createDefaultProfile(name);

        // Assert
        assertNotNull(resultId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createProfile_shouldSaveUserWithAvatarAndReturnId() {
        // Arrange
        String name = "Test User";
        String avatarUrl = "http://avatar.url/image.png";

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            return User.builder()
                    .id(UUID.randomUUID())
                    .name(u.getName())
                    .dateOfBirth(u.getDateOfBirth())
                    .gender(u.getGender())
                    .avatarUrl(u.getAvatarUrl())
                    .build();
        });

        // Act
        UUID resultId = userInternalService.createProfile(name, avatarUrl);

        // Assert
        assertNotNull(resultId);
        verify(userRepository).save(any(User.class));
    }
}
