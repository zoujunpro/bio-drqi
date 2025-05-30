package com.bio.drqi.tc.req;

import com.bio.drqi.common.dto.PageDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TcHarvestListPageReqDTO extends PageDTO {
    /**
     * 取样批次号
     */
    private String sampleApplyNum;

    /**
     * 授粉批次号
     */
    private String pollinationApplyNum;

    /**
     * 母本小区编号
     */
    @JsonProperty("mRegionNum")
    private String mRegionNum;

    /**
     * 父本小区编号
     */
    @JsonProperty("fRegionNum")
    private String fRegionNum;

    /**
     * 母本单株编号
     */
    @JsonProperty("mSampleCode")
    private String mSampleCode;

    /**
     * 父本单株编号
     */
    @JsonProperty("fSampleCode")
    private String fSampleCode;

    /**
     * 母本种子编号
     */
    @JsonProperty("mSeedNum")
    private String mSeedNum;

    /**
     * 父本种子编号
     */
    @JsonProperty("fSeedNum")
    private String fSeedNum;



}
