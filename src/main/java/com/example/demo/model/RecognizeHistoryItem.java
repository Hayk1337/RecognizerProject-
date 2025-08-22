package com.example.demo.model;

import lombok.Data;

import java.util.Date;

@Data
public class RecognizeHistoryItem {
    private Long id;
    private String name;
    private Date recognizeDate;
}
