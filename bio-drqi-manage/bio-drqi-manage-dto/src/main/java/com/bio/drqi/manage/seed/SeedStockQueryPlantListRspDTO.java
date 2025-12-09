package com.bio.drqi.manage.seed;

import lombok.Data;

import java.util.Date;

@Data
public class SeedStockQueryPlantListRspDTO {
    /**
     * 申请工单
     */
    private String taskNum;
    /**
     * 渠道 CER 大田
     */
    private String sourceCode;

    private String plantUserName;

    private Date createTime;

    private String regionCode;

    private String seedNum;

    private String plantNumber;

}
