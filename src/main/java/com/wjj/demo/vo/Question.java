package com.wjj.demo.vo;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class Question {
    private String id;
    private String content;
    private String author;
    private ConcurrentHashMap<String,Answer> answerMap;
    private int answerNumber = answerMap==null?0:answerMap.size();

}
