package com.bio.drqi.vector.rsp;

import lombok.Data;

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
