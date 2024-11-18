package com.bio.cer.vector.rsp;

import lombok.Data;

import java.util.Date;

@Data
public class StepListRspDTO {

    /**
     * 步骤
     */
    private String stepCode;

    private String stepName;
    /**
     *显示 Y亮 N不亮
     */
    private String showFlag;
}
