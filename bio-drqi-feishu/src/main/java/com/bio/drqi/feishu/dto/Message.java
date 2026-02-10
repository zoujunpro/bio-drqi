package com.bio.drqi.feishu.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Message {
    private String title;
    private String content;
    private String url;
    private String time;
    private List<String> msgList=new ArrayList<>();
}
