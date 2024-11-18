package com.bio.drqi.board;

import lombok.Data;

@Data
public class ProjectTaskCountRspDTO {
    /**
     * 总任务
     */
    private Integer totalCountNum;
    /**
     * 我代办任务
     */
    private Integer  pendingCountNum;
    /**
     * 我已办任务
     */
    private Integer  dealCountNum;
    /**
     * 我申请任务
     */
    private Integer applyCountNum;

    private Integer projectCountNum;

    private Integer conversionAndTransCountNum;

    private Integer vectorTaskCountNum;

    private Integer sampleCountNum;

    private Integer transFormCountNum;
}
