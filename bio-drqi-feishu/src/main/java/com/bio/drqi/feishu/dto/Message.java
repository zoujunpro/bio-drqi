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
    private List<Row> rowList=new ArrayList<>();


    @Data
    public static class Row {
        private String productInnerCode;

        private String productName;

        private String stockName;

        private String expirationDate;
    }
}
