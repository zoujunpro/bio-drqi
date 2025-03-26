package com.bio.drqi.bsm.req;


import lombok.Data;

import java.util.List;

@Data
public class BmsPrintProductLabelReqDTO {

    private String taskNum;

    private List<Content> contentList;

    @Data
    public static class  Content{

        private Integer stockInId;


        private Integer printNum;
    }
}
