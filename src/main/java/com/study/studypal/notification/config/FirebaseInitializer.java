package com.study.studypal.notification.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.notification.exception.DeviceTokenErrorCode;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class FirebaseInitializer {
  @Value("${firebase.config.path}")
  private String firebaseConfigPath;

  @PostConstruct
  public void initialize() {
    try {
      InputStream serviceAccount = new ClassPathResource(firebaseConfigPath).getInputStream();

      FirebaseOptions options =
          FirebaseOptions.builder()
              .setCredentials(GoogleCredentials.fromStream(serviceAccount))
              .build();

      if (FirebaseApp.getApps().isEmpty()) {
        FirebaseApp.initializeApp(options);
      }
    } catch (Exception e) {
      throw new BaseException(DeviceTokenErrorCode.PUSH_SERVICE_INIT_ERROR, e.getMessage());
    }
  }
}
