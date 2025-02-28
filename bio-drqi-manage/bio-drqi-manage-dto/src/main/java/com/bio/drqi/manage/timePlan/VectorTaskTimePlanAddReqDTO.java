package com.bio.drqi.manage.timePlan;

import lombok.Data;

@Data
public class VectorTaskTimePlanAddReqDTO {

    /**
     * 实施方案ID
     */
    private String vectorTaskCode;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 预估开始时间
     */
    private String estimatedStartTime;

    /**
     * 预估结束时间
     */
    private String estimatedEndTime;

}
