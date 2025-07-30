package com.study.studypal.services;

import java.util.UUID;

public interface UserService {
    UUID createDefaultProfile(String name);
    UUID createProfile(String name, String avatarUrl);
}
