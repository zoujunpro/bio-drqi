package com.bio.drqi.tc.req;

import com.bio.drqi.common.dto.PageDTO;
import lombok.Data;

import java.util.Date;
@Data
public class TcPollinationListPageReqDTO extends PageDTO {

    /**
     * 实验编号
     */
    private String experimentNum;

    /**
     * 取样批次号
     */
    private String sampleApplyNum;

    /**
     * 授粉方式
     */
    private String pollinationType;

    /**
     * 授粉批次号
     */
    private String pollinationApplyNum;

    /**
     * 收获批次号
     */
    private String harvestApplyNum;

}
