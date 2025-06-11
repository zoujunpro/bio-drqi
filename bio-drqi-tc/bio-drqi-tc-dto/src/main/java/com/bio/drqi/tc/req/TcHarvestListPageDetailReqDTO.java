package com.bio.drqi.tc.req;

import com.bio.drqi.common.dto.PageDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TcHarvestListPageDetailReqDTO extends PageDTO {
    /**
     * 收获批次号
     */
    @NotBlank(message = "收获批次号缺失")
    private String harvestApplyNum;

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
