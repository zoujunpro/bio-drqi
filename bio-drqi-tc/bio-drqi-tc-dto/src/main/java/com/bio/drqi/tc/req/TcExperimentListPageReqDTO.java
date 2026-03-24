package com.bio.drqi.tc.req;

import com.bio.drqi.common.dto.PageDTO;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TcExperimentListPageReqDTO extends PageDTO {
    /*
     * 试验编号
     */
    private String experimentNum;

    /**
     * 取样编号
     */
    private String sampleApplyNum;

    /**
     * 小区编号
     */
    private String regionNum;

    /**
     * 种子编号
     */
    private String seedNum;

    /**
     * 实施方案编号
     */
    private String vectorTaskCode;

    /**
     * 物种
     */
    private String speciesCode;

    /**
     * 品种
     */
    private String breedCode;


    /**
     * PD编号
     */
    private String pdImplementCode;
}
