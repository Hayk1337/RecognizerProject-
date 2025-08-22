package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "RECOGNIZE_HISTORY")
@Data
@NoArgsConstructor
public class RecognizeHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USER", nullable = false)
    private String user;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "RESULT", nullable = false)
    private String result;

    @Column(name = "RECOGNIZE_DATE", nullable = false)
    private Date recognizeDate;

    @Column(name = "DURATION", nullable = false)
    private int duration;

    public RecognizeHistoryEntity(String user, String name, String result, Date recognizeDate, int duration) {
        this.user = user;
        this.name = name;
        this.result = result;
        this.recognizeDate = recognizeDate;
        this.duration = duration;
    }
}
