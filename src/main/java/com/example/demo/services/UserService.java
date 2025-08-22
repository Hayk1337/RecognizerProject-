package com.example.demo.services;

import com.example.demo.entity.User;
import com.example.demo.model.RecognizerResponse;
import com.example.demo.model.UserInfo;
import com.example.demo.repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.example.demo.model.RecognizerError.*;
import static com.example.demo.util.GlobalConstants.EMAIL_REGEXP;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final MailService mailService;
    private final HistoryService historyService;

    @Autowired
    public UserService(UserRepository userRepository, MailService mailService, HistoryService historyService) {
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.historyService = historyService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUser(username);
        if (user == null || !user.isConfirmed()) {
            throw new UsernameNotFoundException("User not found");
        }
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities("USER")
                .build();
    }

    public RecognizerResponse addUser(String username, String password) {
        if (username == null || password == null || password.trim().isEmpty() || username.length() > 254 || password.length() > 254) {
            return new RecognizerResponse(INVALID_INPUT.getMessage());
        }
        if (!username.matches(EMAIL_REGEXP)) {
            return new RecognizerResponse(INVALID_EMAIL.getMessage());
        }
        User user = userRepository.findUser(username);
        if (user != null) {
            if (user.isConfirmed()) {
                return new RecognizerResponse(USER_EXISTS.getMessage());
            } else {
                Claims claims = mailService.parsJWTToken(user.getJWTToken());
                if (claims == null || new Date().getTime() - claims.getIssuedAt().getTime() > 60000) {
                    userRepository.deleteUser(user);
                } else {
                    return new RecognizerResponse(CONFIRM_EMAIL.getMessage());
                }
            }
        }
        String JWTToken = mailService.generateEmailConfirmationToken(username);
        userRepository.addUser(new User(username, new BCryptPasswordEncoder().encode(password), 7200, JWTToken));

        try {
            mailService.sendConfirmEmail(username, JWTToken);
        } catch (Exception e) {
            return new RecognizerResponse(INVALID_EMAIL.getMessage());
        }
        return new RecognizerResponse();
    }

    public boolean confirmUser(String JWTToken) {
        try {
            Claims claims = mailService.parsJWTToken(JWTToken);
            if (claims == null) {
                return false;
            }
            if (!"email_confirmation".equals(claims.get("purpose", String.class))) {
                return false;
            }

            String email = claims.getSubject();
            User user = userRepository.findUser(email);
            if (user == null) {
                return false;
            }
            if (user.isConfirmed()) {
                return false;
            }

            user.setConfirmed(true);
            userRepository.updateUser(user);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public UserInfo getUserInfo(String username) {
        UserInfo userInfo = userRepository.getUserInfo(username);
        userInfo.setRecognizeHistory(historyService.getUserRecognizeHistory(username));
        return userInfo;
    }

    public boolean hasEnoughAvailableSeconds(String username, int requiredSeconds) {
        return userRepository.getAvailableSeconds(username) >= requiredSeconds;
    }

    public int getAvailableSeconds(String username) {
        return userRepository.getAvailableSeconds(username);
    }

    public void decreaseAvailableSeconds(String username, int seconds) {
        userRepository.decreaseAvailableSeconds(username, seconds);
    }
}
