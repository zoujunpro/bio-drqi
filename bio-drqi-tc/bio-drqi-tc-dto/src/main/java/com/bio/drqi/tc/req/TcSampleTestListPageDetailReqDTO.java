package com.bio.drqi.tc.req;

import com.bio.drqi.common.dto.PageDTO;
import lombok.Data;

@Data
public class TcSampleTestListPageDetailReqDTO extends PageDTO {
    /**
     * 申请编号
     */
    private String sampleApplyNum;


    /**
     * 实验编号
     */
    private String experimentNum;

    /**
     * 小区编号
     */
    private String regionNum;

    /**
     * 种子编号
     */
    private String seedNum;

    /**
     * 项目编号
     */
    private String projectCode;

    /**
     * 实施方案编号
     */
    private String vectorTaskCode;

    /**
     * 品种编号
     */
    private String speciesCode;


    private String targetFlag;
}
