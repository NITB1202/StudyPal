package com.study.studypal.exceptions;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter @Setter
public class ErrorResponse {
    private int statusCode;
    private String errorType;
    private String message;
    private String timestamp;

    public ErrorResponse(int statusCode, String errorType, String message) {
        this.statusCode = statusCode;
        this.errorType = errorType;
        this.message = message;
        this.timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
