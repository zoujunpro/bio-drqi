package com.bio.drqi.feishu.dto;

import lombok.Data;

@Data
public class Message {
    private String title;
    private String content;
    private String url;
    private String time;
}
