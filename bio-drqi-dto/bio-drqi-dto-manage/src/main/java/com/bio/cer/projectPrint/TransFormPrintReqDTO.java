package com.bio.cer.projectPrint;

import lombok.Data;

import java.util.List;

@Data
public class TransFormPrintReqDTO {

    private List<Content> contentList;

    @Data
    public static class Content {
        private String vectorTaskCode;
        private String transformCode;
        private Integer printNum;
    }
}
