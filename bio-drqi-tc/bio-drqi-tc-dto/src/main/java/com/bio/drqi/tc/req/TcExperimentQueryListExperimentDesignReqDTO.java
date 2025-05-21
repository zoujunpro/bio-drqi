package com.bio.drqi.tc.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TcExperimentQueryListExperimentDesignReqDTO {
    /*
     * 试验编号
     */
    @NotBlank(message = "参数缺失：试验编号")
    private String experimentNum;

    /**
     * 取样编号
     */
    private String sampleApplyNum;
}
