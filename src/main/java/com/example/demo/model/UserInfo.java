package com.example.demo.model;

import com.example.demo.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class UserInfo {
    private String username;
    private int availableSeconds = -1;
    private List<RecognizeHistoryItem> recognizeHistory;
    // plan

    public UserInfo(User user) {
        this.username = user.getUsername();
        this.availableSeconds = user.getAvailableSeconds();
    }
}
