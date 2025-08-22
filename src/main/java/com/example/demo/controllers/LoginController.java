package com.example.demo.controllers;

import com.example.demo.model.RecognizerResponse;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RestController
public class LoginController {

    private final UserService userService;

    @GetMapping("/login")
    public ModelAndView getLoginPage(Authentication authentication) {
        ModelAndView modelAndView = new ModelAndView();
        if (authentication != null && authentication.isAuthenticated()) {
            modelAndView.setViewName("recognizer");
        } else {
            modelAndView.setViewName("login");
        }
        return modelAndView;
    }

    @PostMapping("/signUp")
    public RecognizerResponse signUp(@RequestBody Map<String, String> params) {
        return userService.addUser(params.get("username"), params.get("password"));
    }

    @GetMapping("/confirm")
    public ModelAndView confirmEmail(@RequestParam("token") String token) {
        if (userService.confirmUser(token)) {
            return new ModelAndView("success");
        } else {
            return new ModelAndView("fail");
        }
    }

    @GetMapping("/")
    public ModelAndView getLoginPageForRoot(Authentication authentication) {
        ModelAndView modelAndView = new ModelAndView();
        if (authentication != null && authentication.isAuthenticated()) {
            modelAndView.setViewName("recognizer");
        } else {
            modelAndView.setViewName("login");
        }
        return modelAndView;
    }

    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
    }
}