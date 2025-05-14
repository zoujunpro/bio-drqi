package com.bio.drqi.tc.rsp;

import lombok.Data;

import java.util.Date;

@Data
public class TcExperimentListNoPollinationRspDTO {
    private Integer id;

    /**
     * 项目编号
     */
    private String projectCodes;

    /**
     * 实施方案编号
     */
    private String vectorTaskCodes;

    /**
     * 物种编码
     */
    private String speciesCode;

    /**
     * 物种名称
     */
    private String speciesName;

    /**
     * 上传附件
     */
    private String fileUrl;

    /**
     * 实验目的
     */
    private String experimentGoal;

    /**
     * 实验地点
     */
    private String experimentAddress;

    /**
     * 申请人
     */
    private String applyUserName;

    /**
     * 申请人iD
     */
    private Integer applyUserId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 实验编号
     */
    private String experimentNum;

    /**
     * 任务编号
     */
    private String taskNum;

    /**
     * 授粉标识
     */
    private String pollinationNum;
}
