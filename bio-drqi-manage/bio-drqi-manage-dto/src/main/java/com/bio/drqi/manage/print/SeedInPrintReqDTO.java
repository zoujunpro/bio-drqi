package com.bio.drqi.manage.print;

import lombok.Data;

import java.util.List;

@Data
public class SeedInPrintReqDTO {

    private List<Content> contentList;

    @Data
    public static class Content {
        private Integer printNum;
        private Integer id;
        private String uniqueCode;

    }


}

