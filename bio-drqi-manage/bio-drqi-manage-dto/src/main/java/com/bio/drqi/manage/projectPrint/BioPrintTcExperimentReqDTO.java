package com.bio.drqi.manage.projectPrint;

import lombok.Data;

import java.util.List;

@Data
public class BioPrintTcExperimentReqDTO {

    private List<Content> contentList;
    @Data
    public static class Content{
        private String regionNum;
        private String seedNum;
        private Integer printNumber;
    }
}
