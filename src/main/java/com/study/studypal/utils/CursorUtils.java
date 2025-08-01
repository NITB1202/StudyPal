package com.study.studypal.utils;

import com.study.studypal.dtos.TeamUser.internal.DecodedCursor;
import com.study.studypal.exceptions.BusinessException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class CursorUtils {
    public static String encodeCursor(int rolePriority, String name, UUID userId) {
        return Base64.getEncoder().encodeToString(
                (rolePriority + "|" + name + "|" + userId).getBytes(StandardCharsets.UTF_8)
        );
    }

    public static DecodedCursor decodeCursor(String encodedCursor) {
        try {
            String decoded = new String(Base64.getDecoder().decode(encodedCursor), StandardCharsets.UTF_8);
            String[] parts = decoded.split("\\|", 3);
            return new DecodedCursor(Integer.parseInt(parts[0]), parts[1], UUID.fromString(parts[2]));
        } catch (Exception e) {
            throw new BusinessException("Failed to decode cursor: " + e.getMessage());
        }
    }
}