package com.bio.drqi.print;

import lombok.Data;

import java.util.List;

@Data
public class SeedOutPrintReqDTO {

    private List<Content> contentList;

    @Data
    public static class Content {
        private String num;
        private Integer printNum;
        private Integer id;

        private String seedNum;
        private String taskNum;
    }


}

