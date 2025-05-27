package com.bio.drqi.tc.rsp;

import lombok.Data;

import java.util.Date;

@Data
public class TcExperimentListAllRspDTO {

    /**
     * 实验编号
     */
    private String experimentNum;

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
}
