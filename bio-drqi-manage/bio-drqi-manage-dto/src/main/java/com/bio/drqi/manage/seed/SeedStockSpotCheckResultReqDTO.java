package com.bio.drqi.manage.seed;


import lombok.Data;

import java.util.List;

@Data
public class SeedStockSpotCheckResultReqDTO {

    private List<Content> contentList;


    @Data
    public static class Content {
        private String seedNum;
        private String spotCheckResult;
    }
}
