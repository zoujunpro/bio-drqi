package com.bio.drqi.bsm.req;


import lombok.Data;

import java.util.List;

@Data
public class BmsPrintProductLabelReqDTO {

    private String taskNum;

    private List<Content> contentList;

    @Data
    public static class  Content{

        private String unitCode;

        private String productInnerCode;

        private String batchNo;

        private String supplierCode;

        private Integer printNum;

        private String produceDate;

        private String stockCode;
    }
}
