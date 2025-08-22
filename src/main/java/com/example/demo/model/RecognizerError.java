package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecognizerError {


    LOW_BALANCE("low_balance"),
    INVALID_FILE("invalid_file"),
    USER_EXISTS("User already exists"),
    INVALID_EMAIL("Invalid email address"),
    INVALID_TOKEN("Invalid or expired url"),
    USER_NOT_FOUND("User not found"),
    USER_CONFIRMED("Email is already confirmed"),
    CONFIRM_EMAIL("Email sent, check inbox"),
    INVALID_INPUT("Invalid input");

    private final String message;

}
