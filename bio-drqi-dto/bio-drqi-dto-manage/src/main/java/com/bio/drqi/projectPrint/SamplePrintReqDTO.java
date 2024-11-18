package com.bio.drqi.projectPrint;

import lombok.Data;

import java.util.List;

@Data
public class SamplePrintReqDTO {

    /**
     * small large
     */
    private String labelType;

    private List<Content> contentList;

    @Data
    public static class Content {
        private String vectorTaskCode;
        private String sampleCode;
    }
}
