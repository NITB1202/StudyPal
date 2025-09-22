package com.study.studypal.notification.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.notification.exception.DeviceTokenErrorCode;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@Configuration
@RequiredArgsConstructor
@Profile("!test")
public class FirebaseInitializer {
  @Value("${firebase.config.path}")
  private String firebaseConfigPath;

  private final ResourceLoader resourceLoader;

  @PostConstruct
  public void initialize() {
    try {
      Resource resource = resourceLoader.getResource(firebaseConfigPath);
      try (InputStream serviceAccount = resource.getInputStream()) {
        FirebaseOptions options =
            FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
          FirebaseApp.initializeApp(options);
        }
      }
    } catch (Exception e) {
      throw new BaseException(DeviceTokenErrorCode.PUSH_SERVICE_INIT_ERROR, e.getMessage(), e);
    }
  }
}
