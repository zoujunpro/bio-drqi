package com.bio.drqi.sample.rsp;

import lombok.Data;

import java.util.Date;

@Data
public class QueryBioInfoSampleTestResultRspDTO {
    /**
     * 材料名称
     */
    private String sampleId;

    /**
     * 生信系统唯一编号
     */
    private String uniqueDbCode;

    /**
     * 测序编号
     */
    private String runId;

    /**
     * HapID
     */
    private String hapId;

    /**
     * 变异类型合计
     */
    private String varType;

    /**
     * 突变方向合计
     */
    private String mutate;

    /**
     * 变异类型占比(%)
     */
    private String ratio;

    private String resultKey;

    private String confirmStatus;
}
