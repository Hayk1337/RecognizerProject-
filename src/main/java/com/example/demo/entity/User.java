package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
public class User {
    @Id
    @Column(name = "EMAIL")
    private String username;
    @Column(name = "AVAILABLE_SECONDS")
    private int availableSeconds;
    @Column(name = "PASSWORD")
    private String password;
    @Column(name = "EMAIL_CONFIRMED")
    private boolean confirmed;
    @Column(name = "JWT_TOKEN")
    private String JWTToken;

    public User(String username, String password, int availableSeconds, String JWTToken) {
        this.username = username;
        this.password = password;
        this.availableSeconds = availableSeconds;
        this.JWTToken = JWTToken;
    }

}
