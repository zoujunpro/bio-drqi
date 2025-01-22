package com.bio.drqi.projectPrint;

import lombok.Data;

import java.util.List;

@Data
public class TissueEmbryoPrintReqDTO {

    private List<Content> contentList;

    @Data
    public static class Content{
        private String vectorTaskCode;
        private String sampleCode;
        private String remark;
        private Integer printNum;
    }
}
