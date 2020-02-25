package com.wjj.demo.vo;

import lombok.Data;

import java.util.concurrent.ConcurrentHashMap;

@Data
public class Answer {
    private String id;
    private String questionId;
    private String content;
    private String author;
    private double price;
    private ConcurrentHashMap<String,Person> personMap;

}
