package com.example.demo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RecognizerResponse {
    private String recognition;
    private String errorMessage;
    private UserInfo userInfo;

    public RecognizerResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
