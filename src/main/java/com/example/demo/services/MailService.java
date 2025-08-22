package com.example.demo.services;


import io.jsonwebtoken.Claims;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import io.jsonwebtoken.security.Keys;

import java.security.Key;

import static com.example.demo.util.GlobalConstants.*;

@Service
public class MailService {

    private final static long EMAIL_CONFIRM_TOKEN_EXPIRE_TIME = 3600000;
    private final static String SECRET_KEY = "vRb/ryIq9fJgK37etX3rTouL9QO/txfE+TtX+9bDNwU=";
    private final static Key SIGNING_KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));


    public void sendConfirmEmail(String email, String JWTToken) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(
                new BasicAuthenticationInterceptor("api", MAILGUN_API_KEY)
        );

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("from", "Recognize.am <noreply@accounts.recognize.am>");
        data.add("to", email);
        data.add("subject", "Email confirmation");
        data.add("html", getEmailConfirmHtml(JWTToken));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(data, headers);

        String response = restTemplate.postForObject(MAILGUN_API_URL, request, String.class);
        System.out.println(response);
    }

    public Claims parsJWTToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SIGNING_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    private String getEmailConfirmHtml(String JWTToken) {
        String confirmationUrl = "https://recognize.am/confirm?token=" + JWTToken;

        return """
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Welcome to Recognize.am - Complete Your Registration</title>
                    <link href="https://fonts.googleapis.com/css2?family=Orbitron:wght@700&display=swap" rel="stylesheet">
                </head>
                <body style="margin:0; padding:0; font-family: Arial, sans-serif; background-color: #f5f5f5;">
                    <table width="100%%" cellpadding="0" cellspacing="0" role="presentation" style="background-color: #f5f5f5;">
                        <tr>
                            <td align="center" style="padding: 40px 0;">
                                <table width="600" cellpadding="0" cellspacing="0" role="presentation" style="background-color: #ffffff; border-radius: 16px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);">
                                    <!-- Header -->
                                    <tr>
                                        <td style="background-color: #000000; padding: 40px; text-align: center; border-radius: 16px 16px 0 0;">
                                            <h1 style="color: #ffffff; font-size: 38px; margin: 0 0 10px 0; font-family: 'Orbitron', sans-serif; letter-spacing: 1px; text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);">Recognize.am</h1>
                                            <p style="color: #ffffff; font-size: 16px; margin: 0;">Advanced AI Speech Recognition</p>
                                        </td>
                                    </tr>
                
                                    <!-- Content -->
                                    <tr>
                                        <td style="padding: 40px;">
                                            <h2 style="color: #000000; font-size: 24px; text-align: center; margin: 0 0 35px 0;">Welcome to Recognize.am!</h2>
                                           \s
                                            <div style="text-align: center; max-width: 450px; margin: 0 auto;">
                                                <p style="color: #333333; font-size: 16px; line-height: 24px; margin: 0 0 20px 0;">Thank you for joining us! We provide advanced AI speech-to-text solutions to make your transcription tasks easier.</p>
                                               \s
                                                <p style="color: #333333; font-size: 16px; line-height: 24px; margin: 0 0 30px 0;">To begin using our services, please confirm your registration:</p>
                                            </div>
                                           \s
                                            <!-- Button -->
                                            <table width="100%%" cellpadding="0" cellspacing="0" role="presentation">
                                                <tr>
                                                    <td align="center" style="padding: 20px 0 30px;">
                                                        <table cellpadding="0" cellspacing="0" role="presentation">
                                                            <tr>
                                                                <td style="background-color: #000000; border-radius: 12px;">
                                                                    <a href="%s" style="display: inline-block; padding: 18px 36px; color: #ffffff; text-decoration: none; font-weight: bold; font-size: 16px; text-transform: uppercase; letter-spacing: 0.5px;">ACTIVATE YOUR ACCOUNT</a>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                            </table>
                
                                            <!-- Legal Text -->
                                            <table width="100%%" cellpadding="0" cellspacing="0" role="presentation" style="background-color: #f8f8f8; border-radius: 12px; margin: 20px 0;">
                                                <tr>
                                                    <td style="padding: 25px;">
                                                        <p style="color: #666666; font-size: 12px; line-height: 18px; text-align: center; margin: 0;">By completing your registration, you acknowledge that you have read and agree to our Terms of Service and Privacy Policy.</p>
                                                    </td>
                                                </tr>
                                            </table>
                
                                            <!-- Footer -->
                                            <table width="100%%" cellpadding="0" cellspacing="0" role="presentation" style="border-top: 1px solid #e0e0e0; margin-top: 30px;">
                                                <tr>
                                                    <td style="padding: 30px 0; text-align: center;">
                                                        <p style="color: #666666; font-size: 12px; margin: 0 0 10px 0;">Need assistance? We're here to help!</p>
                                                        <p style="color: #666666; font-size: 12px; margin: 0 0 10px 0;">Contact our support team at <span style="font-weight: 700;">support@recognize.am</span></p>
                                                        <p style="color: #666666; font-size: 12px; margin: 0;">© 2025 Recognize.am. All rights reserved.</p>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>""".formatted(confirmationUrl);
    }


    public String generateEmailConfirmationToken(String email) {
        long currentTimeMillis = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(email)
                // Добавляем набор claims по необходимости
                .claim("purpose", "email_confirmation")
                // Текущее время (iat)
                .setIssuedAt(new Date(currentTimeMillis))
                // Истекает через expirationMillis
                .setExpiration(new Date(currentTimeMillis + EMAIL_CONFIRM_TOKEN_EXPIRE_TIME))
                // Подпись токена секретным ключом
                .signWith(SIGNING_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

}


//"""
//Пользователь                                   Сервер
//   |                                              |
//1. Ввод данных в форму регистрации                |
//   |--------------------------------------------->|
//2. Отправка данных на сервер (POST /register)     |
//   |                                              |
//   |                         Сохранение данных пользователя
//   |                         (неактивный статус)
//   |                                              |
//   |                         Генерация токена подтверждения
//   |                         email (UUID или JWT)
//   |                                              |
//   |                         Отправка email с подтверждением
//   |                         (SMTP) с токеном в ссылке
//   |<---------------------------------------------|
//3. Получение email и переход по ссылке            |
//   |--------------------------------------------->|
//4. GET /confirm?token=...                         |
//   |                                              |
//   |                         Проверка токена: валидность, срок действия
//   |                         Активация пользователя (статус "активен")
//   |                                              |
//   |                         Возвращение ответа об успешной активации
//   |<---------------------------------------------|
//5. Уведомление об успешной активации              |
//   |                                              |
//"""
