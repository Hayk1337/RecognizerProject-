package com.example.demo.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {

    private static final String secretKey = "f2c7e8fa634f48ecb94c50eb5f4e7154439ef5fc7bb06b1162ef0b4dfe5db41f";
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/signUp", "/confirm").permitAll()

                        .anyRequest().authenticated() // Остальные запросы требуют аутентификации
                )
                .formLogin(login -> login
                        .loginPage("/login") // Настраиваем собственную страницу входа
                        .defaultSuccessUrl("/recognizer", true) // Успешный вход — перенаправление на /home
                        .permitAll().failureHandler((request, response, exception) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL для выхода
                        .logoutSuccessUrl("/login?logout") // URL после успешного выхода
                        .permitAll()
                )
                // Добавляем remember-me
                .rememberMe(rememberMe -> rememberMe
                        .key(secretKey)                 // ключ для генерации токенов
                        .tokenValiditySeconds(1209600)      // 14 дней = 1209600 секунд
                        .alwaysRemember(true)               // всегда запоминать (не ждать галочки)
                )
                // Настройки session management (опционально)
                .sessionManagement(session -> session
                        .sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::newSession)
                        // При необходимости: сколько может быть активных сессий и т.д.
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Шифрование паролей
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}