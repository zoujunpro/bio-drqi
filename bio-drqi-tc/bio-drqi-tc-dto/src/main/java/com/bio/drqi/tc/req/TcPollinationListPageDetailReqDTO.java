package com.bio.drqi.tc.req;

import com.bio.drqi.common.dto.PageDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TcPollinationListPageDetailReqDTO extends PageDTO {

    /**
     * 授粉批次号
     */
    private String pollinationApplyNum;

    /**
     * 实验编号
     */
    private String experimentNum;

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

    @JsonProperty("mSingleNumber")
    private String mSingleNumber;

    @JsonProperty("fSingleNumber")
    private String fSingleNumber;

}
