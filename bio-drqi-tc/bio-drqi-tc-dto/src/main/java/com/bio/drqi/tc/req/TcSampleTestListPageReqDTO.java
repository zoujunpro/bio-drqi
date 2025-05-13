package com.bio.drqi.tc.req;

import com.bio.drqi.common.dto.PageDTO;
import lombok.Data;

@Data
public class TcSampleTestListPageReqDTO extends PageDTO {


    /**
     * 申请编号
     */
    private String sampleApplyNum;

    /**
     * 实验编号
     */
    private String experimentCode;

    /**
     * 取样组织
     */
    private String sampleOrganize;

    /**
     * 取样类型 F首次取样   R重复取样
     */
    private String applyType;
}
