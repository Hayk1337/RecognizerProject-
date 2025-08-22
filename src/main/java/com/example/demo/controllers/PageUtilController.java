package com.example.demo.controllers;

import com.example.demo.model.UserInfo;
import com.example.demo.services.HistoryService;
import com.example.demo.services.MediaService;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class PageUtilController {
    private final UserService userService;
    private final HistoryService historyService;
    private final MediaService mediaService;

    @Autowired
    public PageUtilController(UserService userService, HistoryService historyService, MediaService mediaService) {
        this.userService = userService;
        this.historyService = historyService;
        this.mediaService = mediaService;
    }

    @GetMapping("/userDetails")
    public UserInfo getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getUserInfo(userDetails.getUsername());
    }

    @GetMapping("/historyItem")
    public Map<String, String> getHistoryItemResult(@RequestParam String id) {
        return Map.of("result", historyService.getHistoryItemResult(id));
    }

    @GetMapping("/fbVideoPreview")
    public Map<String, String> getFacebookVideoPreviewURL(@RequestParam String url) {
        Map<String, String> result = new HashMap<>();
        result.put("result", mediaService.getFacebookVideoPreview(url));
        return result;    }
}
