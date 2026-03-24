package com.bio.drqi.tc.req;

import com.bio.drqi.common.dto.PageDTO;
import lombok.Data;

@Data
public class TcExperimentApplyListPageReqDTO extends PageDTO {

    /**
     * 项目编号
     */
    private String projectCode;

    /**
     * 实施方案编号
     */
    private String vectorTaskCode;

    /**
     * 物种编码
     */
    private String speciesCode;

    /**
     * 实验编号
     */
    private String experimentNum;
}
